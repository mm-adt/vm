## Building Documentation

Documentation for mm-ADT is generated through Asciidoc. To enhance the formatting of mm-ADT examples a custom syntax highlighter was developed to plug into Asciidoc infrastructure. As a result, it is necessary to have custom builds of three components which produce mm-ADT specific artifacts. The forks of these repositories can be found here:

* [Coderay](https://github.com/mm-adt/coderay)
* [Asciidoctorj](https://github.com/mm-adt/asciidoctorj)
* [sbt-site](https://github.com/mm-adt/sbt-site)

The mm-ADT project does not publish these artifacts, therefore, those wishing to build mm-ADT documentation must build them locally so that the artifacts are present in that environment. Unfortunately, these three projects are based on three different development ecosystems (Scala, Gradle, and Ruby) so the environmental prerequisites are a bit heavy handed:

* Java
* [Bundler](https://bundler.io/)
* [sbt](https://www.scala-sbt.org/)
* Clone the three repositories listed above

Each of the forked repositories has a branch based on a specific version of its respective project that is known to work together with the other components. For each fork, checkout the appropriate branch:

```bash
coderay$ git checkout 1.1.0-mmadt
asciidoctorj$ git checkout 1.6.2-mmadt
sbt-site$ git checkout 1.4.0-mmadt
```

Build each of these projects with the following commands:

```bash
coderay$ bundle exec rake clean install:local
asciidoctorj$ ./gradlew clean publishToMavenLocal -Pskip.signing
sbt-site$ clean sbt publishLocal
```

The above commands will install each of these components to their respective local repositories so that they can each reference each other via their build tooling. If all of this publishing completed successfully, then it should be possible to generate the documentation:

```text
sbt:vm> previewSite
```

Maintaining these forks should not be overly burdensome given the lightweight nature of the changes in each repository. Ideally, tagged versions in the parent repositories should be used as bases for new branches in each of their respective forks. Versions should be aligned carefully to ensure that the dependency version of the tag matches the ones of the branches. So, if Asciidoctorj 2.0.0 uses Coderay 1.5.0, then branches should be created for `2.0.0-mmadt` in the Asciidoctorj fork and `1.5.0-mmadt` in the Coderay fork. At that point, the mm-ADT specific changes could be cherry-picked to those new mm-ADT branches.
