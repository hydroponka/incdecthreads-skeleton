call mvn -Dtest=ConcurrentThreadsTest test

for /l %%i IN (1,1,5) DO (call mvn -Dtest=ConcurrentThreadsTest -Dskip=true test)