package com.example.zuulgateway.filter;

import com.example.zuulgateway.utils.JwtUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationZuulFilter extends ZuulFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String filterType() {
        return "pre"; // This is a pre-filter, runs before routing
    }

    @Override
    public int filterOrder() {
        return 0; // Execute early in the filter chain
    }

    @Override
    public boolean shouldFilter() {
        // Apply this filter to all routes EXCEPT the auth service endpoints
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String requestURI = request.getRequestURI();

        // Skip auth endpoints like /auth/login, /auth/register etc.
        return !requestURI.startsWith("/auth/");
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;
        String userId = null;
        String userType = null; // To hold the user type string

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                // Validate token and extract claims
                Claims claims = jwtUtil.extractAllClaims(jwt); // Assuming JwtUtil has this method
                username = claims.getSubject();

                // If token is valid and user info extracted
                if (username != null) {
                    userId = claims.get("userId", String.class); // Get userId from claims
                    userType = claims.get("userType", String.class); // Get userType from claims
                    // Create Spring Security Authentication object
                    // Note: We are not loading UserDetails from a UserDetailsService here,
                    // we are creating an authenticated token directly based on JWT claims.
                    // This is because Zuul's primary role is token validation and propagation,
                    // not full user detail loading from a DB (that's auth-server's job).

                    Collection<? extends GrantedAuthority> authorities = Collections.emptyList(); // Default empty
                    if (userType != null && !userType.trim().isEmpty()) {
                        authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_" + userType.toUpperCase()) // Ensure ROLE_ prefix and uppercase
                        );
                    }


                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            // Principal: username or UserDetails object
                            username,
                            // Credentials: null or empty after authentication
                            null, // Or ""
                            // Authorities: User roles/permissions
                            authorities
                    );

                    // Set the authentication object in Spring Security Context
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    // *** Add user information to request headers for downstream services ***
                    context.addZuulRequestHeader("X-User-Id", userId);
                    context.addZuulRequestHeader("X-User-Name", username);
                    // Pass roles as a comma-separated string
                    context.addZuulRequestHeader("X-User-Roles", authorities.stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(",")));

                    System.out.println("Zuul Filter: Authenticated user: " + username + ", Roles: " + userType);
                    System.out.println("Zuul Filter: Added headers: X-User-Id=" + userId + ", X-User-Name=" + username + ", X-User-Roles=" + context.getRequest().getHeader("X-User-Roles")); // Check headers in context's request


                }else {
                    // Token is valid, but username claim is missing - treat as invalid authentication
                    System.err.println("Zuul Filter: JWT validated but subject (username) claim is missing.");
                    // Optionally set error response for missing username
                    // context.setSendZuulResponse(false);
                    // context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
                    // context.setResponseBody("Token missing username claim");
                }
            } catch (Exception e) {
                // Log token validation failure (optional, but good for debugging)
                System.err.println("Zuul Filter: JWT validation failed: " + e.getMessage());
                // Optionally set response status code for invalid token
                // context.setSendZuulResponse(false);
                // context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
                // context.setResponseBody("Invalid or expired token");
                context.setSendZuulResponse(false);
                context.setResponseStatusCode(401); // Use 401 Unauthorized for authentication failure
                context.setResponseBody("Invalid or expired token");
            }
        } else {
            // No Authorization header or not starting with Bearer
            // Request proceeds without authentication in Zuul.
            // Downstream services can then decide if authentication is required for the specific endpoint.
            System.out.println("Zuul Filter: No JWT found in Authorization header or invalid format..");
        }

        return null; // Continue the filter chain
    }
}
