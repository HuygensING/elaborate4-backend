#!/usr/bin/env bash
cd elab4-backend && (
  cat pom.xml | sed -e "s/packaging>war/packaging>jar/" >/tmp/jarpom.xml
  mvn install -f /tmp/jarpom.xml
)
