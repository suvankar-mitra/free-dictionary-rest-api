package cc.suvankar.free_dictionary_api.filter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter implements Filter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private static final Logger LOG = Logger.getLogger(RateLimitFilter.class.getName());

    private Bucket resolveBucket(String clientIp) {
        return buckets.computeIfAbsent(clientIp, k -> Bucket.builder()
                .addLimit(Bandwidth.builder()
                    .capacity(30)
                    .refillGreedy(30, Duration.ofMinutes(1))
                    .build())
                .build());
    }

    private Bucket resolveBucketForWordOfTheDay(String clientIp) {
        String key = clientIp + ":wordoftheday";
        return buckets.computeIfAbsent(key, k -> Bucket.builder()
                .addLimit(Bandwidth.builder()
                    .capacity(5)
                    .refillGreedy(5, Duration.ofMinutes(1))
                    .build())
                .build());
    }

    private void setRateLimitHeaders(HttpServletResponse response, Bucket bucket, long limit) {
        long remaining = bucket.estimateAbilityToConsume(1).getRemainingTokens() > 0 
            ? Math.max(0, bucket.estimateAbilityToConsume(1).getRemainingTokens() - 1)
            : 0;
        long resetTime = Instant.now().plusSeconds(60).getEpochSecond();

        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        response.setHeader("X-RateLimit-Reset", String.valueOf(resetTime));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String path = httpRequest.getRequestURI();

        // For wordoftheday
        if (path != null && path.endsWith("/wordoftheday")) {
            String clientIp = httpRequest.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isEmpty()) {
                clientIp = request.getRemoteAddr();
            } else {
                // if there are multiple IPs, take the first one
                if (clientIp.contains(",")) {
                    clientIp = clientIp.split(",")[0].trim();
                }
            }

            Bucket bucket = resolveBucketForWordOfTheDay(clientIp);
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            if (bucket.tryConsume(1)) {
                setRateLimitHeaders(httpResponse, bucket, 5);
                chain.doFilter(request, response);
            } else {
                setRateLimitHeaders(httpResponse, bucket, 5);
                httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too Many Requests");
                
                String userAgent = ((HttpServletRequest)request).getHeader("User-Agent");
                if (clientIp == null) {
                    clientIp = request.getRemoteAddr();
                }
                LOG.log(Level.INFO,"Too many requests= from IP: {1}, userAgent:{2}", new Object[]{clientIp, userAgent});
            }
            return;
        }

        // For definition/search/translation requests
        if (path != null && (path.startsWith("/dictionaryapi/v1/definitions")
                || path.startsWith("/dictionaryapi/v1/words")
                || path.startsWith("/dictionaryapi/v1/translations"))) {
            String clientIp = httpRequest.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isEmpty()) {
                clientIp = request.getRemoteAddr();
            } else {
                if (clientIp.contains(",")) {
                    clientIp = clientIp.split(",")[0].trim();
                }
            }

            Bucket bucket = resolveBucket(clientIp);
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            if (bucket.tryConsume(1)) {
                setRateLimitHeaders(httpResponse, bucket, 30);
                chain.doFilter(request, response);
            } else {
                setRateLimitHeaders(httpResponse, bucket, 30);
                httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too Many Requests");

                String userAgent = ((HttpServletRequest)request).getHeader("User-Agent");
                if (clientIp == null) {
                    clientIp = request.getRemoteAddr();
                }
                LOG.log(Level.INFO,"Too many requests= from IP: {1}, userAgent:{2}", new Object[]{clientIp, userAgent});
            }
            return;
        }

        // For other endpoints
        chain.doFilter(request, response);
    }
}
