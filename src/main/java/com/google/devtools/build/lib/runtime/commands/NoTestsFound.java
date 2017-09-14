// Copyright 2014 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.runtime.commands;

import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.buildeventstream.BuildEvent;
import com.google.devtools.build.lib.buildeventstream.BuildEventConverters;
import com.google.devtools.build.lib.buildeventstream.BuildEventId;
import com.google.devtools.build.lib.buildeventstream.BuildEventStreamProtos;
import com.google.devtools.build.lib.buildeventstream.GenericBuildEvent;
import com.google.devtools.build.lib.util.ExitCode;
import java.util.Collection;

/** This event is posted by the {@link TestCommand} if no tests were found. */
public class NoTestsFound implements BuildEvent {
  private final ExitCode exitCode;
  private final long finishTimeMillis;

  public NoTestsFound(ExitCode exitCode, long finishTimeMillis) {
    this.exitCode = exitCode;
    this.finishTimeMillis = finishTimeMillis;
  }

  public ExitCode getExitCode() {
    return exitCode;
  }

  @Override
  public BuildEventId getEventId() {
    return BuildEventId.buildFinished();
  }

  @Override
  public Collection<BuildEventId> getChildrenEvents() {
    return ImmutableList.of();
  }

  @Override
  public BuildEventStreamProtos.BuildEvent asStreamProto(BuildEventConverters converters) {
    BuildEventStreamProtos.BuildFinished.ExitCode protoExitCode =
        BuildEventStreamProtos.BuildFinished.ExitCode.newBuilder()
            .setName(exitCode.name())
            .setCode(exitCode.getNumericExitCode())
            .build();

    BuildEventStreamProtos.BuildFinished finished =
        BuildEventStreamProtos.BuildFinished.newBuilder()
            .setOverallSuccess(ExitCode.SUCCESS.equals(exitCode))
            .setExitCode(protoExitCode)
            .setFinishTimeMillis(finishTimeMillis)
            .build();
    return GenericBuildEvent.protoChaining(this).setFinished(finished).build();
  }
}
