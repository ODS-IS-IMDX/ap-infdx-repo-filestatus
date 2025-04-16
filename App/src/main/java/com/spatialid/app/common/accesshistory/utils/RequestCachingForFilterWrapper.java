// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.accesshistory.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * フィルター処理におけるリクエストデータのキャッシングを行うクラス．<br>
 * コンストラクタ呼び出し時に{@link HttpServletRequest}から取得したリクエストボディをバイト配列として保持する．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2025/01/15
 */
public class RequestCachingForFilterWrapper extends HttpServletRequestWrapper {
    
    /**
     * キャッシュされたリクエストボディ．
     */
    private final byte[] cachedBody;
    
    public RequestCachingForFilterWrapper(HttpServletRequest request) throws IOException {
        
        super(request);
        
        cachedBody = request.getInputStream().readAllBytes();
        
    }
    
    /**
     * キャッシュされたリクエストボディから{@link ServletInputStream}のラッパークラスである{@link CachedServletInputStream}を生成して返却する．
     * <p>
     * 自クラスに保持したリクエストボディを基に{@link CachedServletInputStream}が生成されるため、複数回の呼び出しに対応している．
     * </p>
     * 
     * @return ServletInputStream {@link CachedServletInputStream}でラップされたリクエストボディのStream
     */
    @Override
    public ServletInputStream getInputStream() {
        
        return new CachedServletInputStream(cachedBody);
        
    }
    
    /**
     * スーパークラスである{@link HttpServletRequest}からリクエストパラメータをマップとして取得して返却する．
     * <p>
     * Getリクエストのみの想定であり、リクエストパラメータのキャッシュは行わない．<br>
     * application/x-www-form-urlencoded形式には対応していない．
     * </p>
     * 
     * @return Map<String, String[]> マップとして取得したリクエストパラメータ
     */
    @Override
    public Map<String, String[]> getParameterMap(){
        
        return super.getParameterMap();
        
    }
    
    /**
     * キャッシュされたリクエストデータを基にInputStreamの生成・操作を行う内部クラス．
     * 
     * @author matsumoto kentaro
     * @version 1.1 2025/01/15
     */
    private class CachedServletInputStream extends ServletInputStream {
        
        /**
         * キャッシュされたリクエストデータから生成されるINputStream．
         */
        private final ByteArrayInputStream inputStream;
        
        public CachedServletInputStream(byte[] cachedBody) {
            
            this.inputStream = new ByteArrayInputStream(cachedBody);
            
        }
        
        @Override
        public int available() {
            
            return inputStream.available();
            
        }
        
        @Override
        public int read() {
            
            return inputStream.read();
            
        }
        
        @Override
        public int read(byte[] b, int off, int len) {
            
            return inputStream.read( b, off, len );
            
        }

        @Override
        public boolean isFinished() {
            
            return available() == 0;
            
        }

        @Override
        public boolean isReady() {
            
            return true;
            
        }

        @Override
        public void setReadListener(ReadListener listener) {
            
            throw new UnsupportedOperationException();
            
        }
        
    }
    
    

}
