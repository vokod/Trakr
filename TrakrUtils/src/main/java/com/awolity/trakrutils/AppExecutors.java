/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.awolity.trakrutils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public class AppExecutors {

    private final Executor diskIO;

    private final Executor transformationExecutor;

    private AppExecutors(Executor diskIO, Executor transformationExecutor) {
        this.diskIO = diskIO;
        this.transformationExecutor = transformationExecutor;
    }

    public AppExecutors() {
        this(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor());
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor getTransformationExecutor(){
        return transformationExecutor;
    }
}
