## Clocker CLI

A CLI application to register and list timestamps. This app is used to demo Scalar DB coupled with a
Postgresql database.

### To generate an executable

```
./gradlew installDist
```

### Manage the environment

To start the environment
```aidl
cd env
./env up
```

To stop and clean up the environment
```aidl
cd env
./env down
```

### Run
```
./clocker-cli/build/install/clocker-cli/bin/clocker-cli
Usage: clocker [-hV] [COMMAND]
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  register  Register a timestamp.
            Example : register vincent sapporo
  list      List registered timestamps for the given date.
            Example : list 2021-07-08
```
#### Available commands

Register a timestamp
```aidl
clocker-cli/build/install/clocker-cli/bin/clocker-cli register vincent sapporo
{
  "date" : "2021-07-08",
  "time" : "11:14:52.048",
  "user" : "vincent",
  "location" : "sapporo",
  "createdAt" : "2021-07-08T11:14:52.048"
}
```

List registered timestamps for the given day
```
clocker-cli/build/install/clocker-cli/bin/clocker-cli list 2021-07-08
[ {...},    
{
  "date" : "2021-07-08",
  "time" : "11:14:52.048",
  "user" : "vincent",
  "location" : "sapporo",
  "createdAt" : "2021-07-08T11:14:52.048"
} ]
```


