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

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

/**
 * This interface indicate that it can create custom RunnableFuture instance instead of normal FutureTask.
 *
 * @author huangjinfu
 */

interface CustomFutureCallable<T> extends Callable<T> {
    /**
     * Create a RunnableFuture instance for this Callable instance.
     *
     * @return
     */
    RunnableFuture<T> newTaskFor();
}
