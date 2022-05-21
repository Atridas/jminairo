javac -d ./build ./cat/atridas87/minairo/Minairo.java
cd build
jar cvf jminairo.jar *
jar cfm jminairo.jar ../Manifest.txt *
move jminairo.jar ../stage/
