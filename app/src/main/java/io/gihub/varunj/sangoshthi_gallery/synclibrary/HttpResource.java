/*
 * Copyright 2017 huangjinfu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gihub.varunj.sangoshthi_gallery.synclibrary;

/**
 * @author huangjinfu
 */

class HttpResource extends Resource {

    private static final long serialVersionUID = 1L;

    private final long contentLength;
    private final String acceptRanges;
    private final String eTag;
    private final String lastModified;

    public HttpResource(long contentLength, String acceptRanges, String eTag, String lastModified) {
        this.contentLength = contentLength;
        this.acceptRanges = acceptRanges;
        this.eTag = eTag;
        this.lastModified = lastModified;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getAcceptRanges() {
        return acceptRanges;
    }

    public String geteTag() {
        return eTag;
    }

    public String getLastModified() {
        return lastModified;
    }
}
