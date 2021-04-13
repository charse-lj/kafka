/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.utils;

import org.apache.kafka.common.errors.TimeoutException;

import java.util.function.Supplier;

/**
 * A time implementation that uses the system clock and sleep call. Use `Time.SYSTEM` instead of creating an instance
 * of this class.
 */
public class SystemTime implements Time {

    @Override
    public long milliseconds() {
        return System.currentTimeMillis();
    }

    @Override
    public long nanoseconds() {
        return System.nanoTime();
    }

    @Override
    public void sleep(long ms) {
        Utils.sleep(ms);
    }

    @Override
    public void waitObject(Object obj, Supplier<Boolean> condition, long deadlineMs) throws InterruptedException {
        //锁
        synchronized (obj) {
            //死循环
            while (true) {
                //condition.get() 为true,跳出循环
                if (condition.get()) {
                    return;
                }

                //当前时间
                long currentTimeMs = milliseconds();
                //当前时间>=截止时间,直接报错
                if (currentTimeMs >= deadlineMs) {
                    throw new TimeoutException("Condition not satisfied before deadline");
                }

                //超时等待一段时间,线程 waiting状态,锁释放 ==>阻塞于此,两种状况会被唤醒
                //1.超时了会自动被唤醒
                //2.被其他现场使用notify()唤醒
                obj.wait(deadlineMs - currentTimeMs);
            }
        }
    }

}
