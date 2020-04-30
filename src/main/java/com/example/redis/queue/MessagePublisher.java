package com.example.redis.queue;

public interface MessagePublisher {

	void publish(final String message);
}
