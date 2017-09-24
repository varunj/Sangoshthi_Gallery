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

import android.support.annotation.MainThread;

/**
 * When task occurs error, this interface can notify this error. Those methods will be called on main thread.
 *
 * @author huangjinfu
 */
@MainThread
public interface ErrorListener {
    /**
     * Notify that some error occurs.
     *
     * @param task
     * @param error
     */
    void onError(Task task, Exception error);
}
