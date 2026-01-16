package com.sleekydz86.chat.global.config

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.vectorstore.redis.RedisVectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import redis.clients.jedis.JedisPooled
import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import org.apache.commons.pool2.impl.GenericObjectPoolConfig

@Configuration
class VectorStoreConfig {

    @Bean
    fun redisVectorStore(
        redisConnectionFactory: RedisConnectionFactory,
        embeddingModel: EmbeddingModel?,
        @Value("\${spring.ai.vectorstore.redis.index:spring-ai-index}") index: String,
        @Value("\${spring.ai.vectorstore.redis.prefix:doc:}") prefix: String
    ): RedisVectorStore {
        if (embeddingModel == null) {
            throw IllegalStateException("EmbeddingModel bean is required for RedisVectorStore. Please configure an embedding model in application.yml")
        }
        val jedisPooled = when (redisConnectionFactory) {
            is LettuceConnectionFactory -> {
                val host = redisConnectionFactory.hostName
                val port = redisConnectionFactory.port
                val password = redisConnectionFactory.password?.toString()
                
                val clientConfig = if (password != null && password.isNotEmpty()) {
                    DefaultJedisClientConfig.builder()
                        .password(password)
                        .build()
                } else {
                    DefaultJedisClientConfig.builder().build()
                }
                
               
                if (password != null && password.isNotEmpty()) {
                    JedisPooled(host, port, null, password)
                } else {
                    JedisPooled(host, port)
                }
            }
            else -> {
                throw IllegalArgumentException("Unsupported RedisConnectionFactory type: ${redisConnectionFactory.javaClass}")
            }
        }
        
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
            .indexName(index)
            .prefix(prefix)
            .build()
    }
}
