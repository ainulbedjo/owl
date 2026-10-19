#!/bin/sh

for f in *.{png,jpg,jpeg}; do cwebp -q 80 "$f" -o "${f%.*}.webp"; done

