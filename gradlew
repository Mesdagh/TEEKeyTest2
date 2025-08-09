$script = @"
#!/usr/bin/env sh
APP_HOME="$(cd "$(dirname "$0")"; pwd -P)"
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
if [ -z "$JAVA_HOME" ]; then
  exec java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
else
  exec "$JAVA_HOME/bin/java" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
fi
"@
# ذخیره با LF و بدون BOM
$script = $script -replace "`r`n","`n"
Set-Content -Path gradlew -Value $script -Encoding ascii -NoNewline
