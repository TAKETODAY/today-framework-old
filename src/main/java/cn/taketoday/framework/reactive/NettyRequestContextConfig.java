package cn.taketoday.framework.reactive;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

/**
 * To help build a {@link NettyRequestContext}
 *
 * <p>
 * User can use this class to customize {@link NettyRequestContext}
 * </p>
 *
 * @author TODAY 2021/3/30 17:46
 */
public class NettyRequestContextConfig {

  /**
   * Should Netty validate HTTP response Header values to ensure they aren't malicious.
   */
  private boolean validateHeaders = false;
  private boolean singleFieldHeaders = true;

  private boolean keepAliveWhenSending = true;

  private HttpVersion httpVersion = HttpVersion.HTTP_1_1;

  private Supplier<HttpHeaders> trailingHeaders;

  private ServerCookieEncoder cookieEncoder = ServerCookieEncoder.STRICT;
  private ServerCookieDecoder cookieDecoder = ServerCookieDecoder.STRICT;

  /**
   * response body initial size
   *
   * @see io.netty.buffer.Unpooled#buffer(int)
   */
  private int bodyInitialSize = 64;
  private Supplier<ByteBuf> responseBody;

  public NettyRequestContextConfig() {
    this(() -> EmptyHttpHeaders.INSTANCE);
  }

  public NettyRequestContextConfig(Supplier<HttpHeaders> trailingHeaders) {
    this.trailingHeaders = trailingHeaders;
  }

  /**
   * Should Netty validate Header values to ensure they aren't malicious.
   */
  public void setValidateHeaders(boolean validateHeaders) {
    this.validateHeaders = validateHeaders;
  }

  public void setSingleFieldHeaders(boolean singleFieldHeaders) {
    this.singleFieldHeaders = singleFieldHeaders;
  }

  public boolean isSingleFieldHeaders() {
    return singleFieldHeaders;
  }

  public boolean isValidateHeaders() {
    return validateHeaders;
  }

  public void setTrailingHeaders(Supplier<HttpHeaders> trailingHeaders) {
    this.trailingHeaders = trailingHeaders;
  }

  public Supplier<HttpHeaders> getTrailingHeaders() {
    return trailingHeaders;
  }

  public void setHttpVersion(HttpVersion httpVersion) {
    this.httpVersion = httpVersion;
  }

  public HttpVersion getHttpVersion() {
    return httpVersion;
  }

  public void setKeepAliveWhenSending(boolean keepAliveWhenSending) {
    this.keepAliveWhenSending = keepAliveWhenSending;
  }

  public boolean isKeepAliveWhenSending() {
    return keepAliveWhenSending;
  }

  public void setCookieDecoder(ServerCookieDecoder cookieDecoder) {
    this.cookieDecoder = cookieDecoder;
  }

  public void setCookieEncoder(ServerCookieEncoder cookieEncoder) {
    this.cookieEncoder = cookieEncoder;
  }

  public ServerCookieDecoder getCookieDecoder() {
    return cookieDecoder == null ? ServerCookieDecoder.STRICT : cookieDecoder;
  }

  public ServerCookieEncoder getCookieEncoder() {
    return cookieEncoder == null ? ServerCookieEncoder.STRICT : cookieEncoder;
  }

  public void setResponseBody(Supplier<ByteBuf> responseBody) {
    this.responseBody = responseBody;
  }

  public Supplier<ByteBuf> getResponseBody() {
    return responseBody;
  }

  /**
   * @return response body initial capacity
   *
   * @see io.netty.buffer.Unpooled#buffer(int)
   */
  public int getBodyInitialSize() {
    return bodyInitialSize;
  }

  /**
   * @param bodyInitialSize
   *         response body initial capacity
   *
   * @see io.netty.buffer.Unpooled#buffer(int)
   */
  public void setBodyInitialSize(int bodyInitialSize) {
    this.bodyInitialSize = bodyInitialSize;
  }
}