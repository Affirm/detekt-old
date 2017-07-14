# __detekt__

The original detekt README can be found [here](https://github.com/arturbosch/detekt)

To build a jar for custom rules, clone this repo and then:
```
gradle build
```

The jar we want will be detekt/affirm-custom-ruleset/build/libs/affirm-custom-ruleset-[version].jar

Rename this jar to "ruleset-[version].jar" and put it in android/, and you'll be good to go.
