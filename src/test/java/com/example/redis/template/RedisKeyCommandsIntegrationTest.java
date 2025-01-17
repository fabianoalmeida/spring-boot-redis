package com.example.redis.template;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.ReactiveKeyCommands;
import org.springframework.data.redis.connection.ReactiveStringCommands;
import org.springframework.data.redis.connection.ReactiveStringCommands.SetCommand;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.SpringBootRedisApplication;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redis.embedded.RedisServerBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringBootRedisApplication.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class RedisKeyCommandsIntegrationTest {

	private static redis.embedded.RedisServer redisServer;

    @Autowired private ReactiveKeyCommands keyCommands;

    @Autowired private ReactiveStringCommands stringCommands;
    
    @BeforeClass
    public static void startRedisServer() throws IOException {
        redisServer = new RedisServerBuilder().port(6379).setting("maxmemory 256M").build();
        redisServer.start();
    }
    
    @AfterClass
    public static void stopRedisServer() throws IOException {
        redisServer.stop();
    }

    @Test
    public void givenFluxOfKeys_whenPerformOperations_thenPerformOperations() {
        Flux<String> keys = Flux.just("key1", "key2", "key3", "key4");

        Flux<SetCommand> generator = keys.map(String::getBytes)
            .map(ByteBuffer::wrap)
            .map(key -> SetCommand.set(key)
                .value(key));

        StepVerifier.create(stringCommands.set(generator))
            .expectNextCount(4L)
            .verifyComplete();

        Mono<Long> keyCount = keyCommands.keys(ByteBuffer.wrap("key*".getBytes()))
            .flatMapMany(Flux::fromIterable)
            .count();

        StepVerifier.create(keyCount)
            .expectNext(4L)
            .verifyComplete();

    }
	
}
