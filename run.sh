#!/bin/bash
cd "$(dirname "$0")"
javac -d bin src/*.java && cd bin && java HexLife
