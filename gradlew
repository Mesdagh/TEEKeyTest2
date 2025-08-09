cd C:\Users\client\Desktop\TEEKeyTest2

# محتوای سالم gradlew
$script = @"
#!/usr/bin/env sh
APP_HOME="$(cd "$(dirname "$0")"; pwd -P)"
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
if [ -n "$JAVA_HOME" ] ; then
  JAVA_EXE="$JAVA_HOME/bin/java"
else
  JAVA_EXE="java"
fi
exec "$JAVA_EXE" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
"@

# ذخیره با Line Ending نوع LF و بدون BOM
$script = $script -replace "`r`n","`n"
Set-Content -Path .\gradlew -Value $script -Encoding ascii -NoNewline
