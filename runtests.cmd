call mvn -Dtest=ConcurrentThreadsTest clean test
echo "~~~~~~~~~~~ tests ~~~~~~~~~~~~~"
FOR /L %%i IN (1,1,20) DO (call mvn -Dtest=ConcurrentThreadsTest -Dskip=true test)