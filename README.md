# SimpleScalaRestApi
This project contains a smple scala rest api using akka-http.
It has simple tests and can be built into a fat jar using the sbt-assembly plugin.

## Docker
You can quickly create a docker container of this application. e.g.:
```
cd docker
docker build --build-arg version=0.0.2 -t damontic/simplescalarestapi:0.0.2  .
```

## Tests
To execute the tests you just execute:
```
sbt test
```

## Running locally
To run locally you just execute:
```
sbt run
```

## Packaging
To package you just execute:
```
sbt assembly
```

