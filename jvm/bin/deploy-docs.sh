#!/bin/bash

cd ..
git commit -a -m "documentation processed and generated"
git push
git checkout gh-pages
cp jvm/machine/target/asciidoctor/index.html .
cd images
cp -rf ../jvm/machine/target/asciidoctor/images/ .
# rm -rf jvm/machine/target
git add **/*.png
git add **/*.svg
cd ..
git commit -a -m "documentation deployed to gh-pages"
git push
git checkout master
cd jvm
