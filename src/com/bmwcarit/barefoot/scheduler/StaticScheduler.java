/*
 * Copyright (C) 2015, BMW Car IT GmbH
 *
 * Author: Sebastian Mattheis <sebastian.mattheis@bmw-carit.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.bmwcarit.barefoot.scheduler;

public class StaticScheduler {
    //全局线程池 初始线程个数为系统可用逻辑处理器个数
    //Runtime.getRuntime().availableProcessors(): Java虚拟机将调用操作系统提供的API来确定可用的处理器数量
    //  返回的是可用的计算资源，而不是CPU物理核心数，对于支持超线程的CPU来说，单个物理处理器相当于拥有两个逻辑处理器，能够同时执行两个线程。
    private static Scheduler scheduler = new Scheduler(Runtime.getRuntime().availableProcessors());

    static public class InlineScheduler {
        private final Group group;
        private final Task task;

        protected InlineScheduler(Group group) {
            this.group = group;
            this.task = null;
        }

        protected InlineScheduler(Task task) {
            this.group = null;
            this.task = task;
        }

        public void spawn(Task task) {
            if (this.task != null) {
                this.task.spawn(task);
            } else {
                this.group.spawn(task);
            }
        }

        public boolean sync() {
            if (this.task != null) {
                return task.sync();
            } else {
                return group.sync();
            }
        }
    }

    public static void reset(int numWorkers) {
        scheduler.shutdown();
        scheduler = new Scheduler(numWorkers);
    }

    public static void reset(int numWorkers, long spintime) {
        scheduler.shutdown();
        scheduler = new Scheduler(numWorkers, spintime);
    }

    public static InlineScheduler scheduler() {
        Task self = scheduler.self();
        if (self != null) {
            return new InlineScheduler(self);
        } else {
            return new InlineScheduler(scheduler.group());
        }
    }

    public static void stop() {
        scheduler.shutdown();
    }
}
