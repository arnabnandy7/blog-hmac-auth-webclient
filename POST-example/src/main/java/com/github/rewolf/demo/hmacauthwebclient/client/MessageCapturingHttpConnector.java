package com.github.rewolf.demo.hmacauthwebclient.client;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

/**
 * Http Connector that acts as a hook to supply the ClientHttpRequest to the data encoder for the purpose of
 * signature header injection.
 *
 * @author rewolf
 */
public class MessageCapturingHttpConnector extends ReactorClientHttpConnector {
    private final ThreadLocal<ClientHttpRequest> request = new ThreadLocal<>();

    public Optional<ClientHttpRequest> getRequest() {
        return Optional.ofNullable(request.get());
    }

    @Override
    public Mono<ClientHttpResponse> connect(final HttpMethod method, final URI uri,
                                            final Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
        // execute the super-class method as usual, but insert an interception into the requestCallback that can
        // capture the request to be saved for this thread.
        return super.connect(method, uri, incomingRequest -> {
            this.request.set(incomingRequest);
            return requestCallback.apply(incomingRequest);
        });
    }
}
