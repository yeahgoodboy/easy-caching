package com.googlecode.easyec.cache.rest;

import com.googlecode.easyec.cache.CacheElement;
import com.googlecode.easyec.cache.CacheException;
import com.googlecode.easyec.cache.ehcache.internal.EhcacheDefaultCacheProvider;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Created with IntelliJ IDEA.
 * User: JunJie
 * Date: 12-8-7
 * Time: 上午12:43
 * To change this template use File | Settings | File Templates.
 */
public class RestfulEhcacheCacheProvider extends EhcacheDefaultCacheProvider {

    public RestfulEhcacheCacheProvider(CacheManager cacheManager) {
        super(cacheManager);
    }

    public boolean put(String cacheName, Object cacheKey, Object value) throws CacheException {
        try {
            if (value != null) {
                if (value instanceof CacheElement) {
                    return put(cacheName, (CacheElement) value);
                }

                if (cacheKey == null) {
                    throw new CacheException("cache key is null.");
                }

                Cache cache = cacheManager.getCache(cacheName);
                if (value instanceof Element) {
                    cache.put((Element) value);
                } else {
                    cache.put(new Element(cacheKey, value));
                }

                return true;
            }

            logger.warn("Value of key: [" + cacheKey + "] is null when put into cache: [" + cacheName + "].");
            return false;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public boolean put(String cacheName, CacheElement element) throws CacheException {
        try {
            if (element != null) {
                if (element.getKey() == null) {
                    throw new CacheException("cache key is null.");
                }

                Element e = new Element(element.getKey(), element);
                if (element.getTimeToIdle() > 0) {
                    e.setTimeToIdle(element.getTimeToIdle());
                }

                if (element.getTimeToLive() > 0) {
                    e.setTimeToLive(element.getTimeToLive());
                }

                cacheManager.getCache(cacheName).put(e);
                return true;
            }

            logger.warn("Cache element is null when put into cache: [" + cacheName + "].");
            return false;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Object get(String cacheName, Object cacheKey) throws CacheException {
        try {
            Element element = cacheManager.getCache(cacheName).get(cacheKey);
            if (element != null) {
                Object o = element.getValue();
                if (o instanceof CacheElement) {
                    CacheElement e = (CacheElement) o;
                    e.setHitCount(element.getHitCount());
                    e.setLastUpdateTime(element.getLastUpdateTime());
                }

                return o;
            }

            return null;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }
}
