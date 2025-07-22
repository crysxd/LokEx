# LokEx
LokEx is a utility tool to export strings from Lokalise using the REST API using a Jinja templating language. 
You can use it as Java library, CLI tool or gradle plugin

Features:
- Support for different languages per project
- Code generation using Jinja2 templating syntax for any code language
- Simple configuration

## How to use LokEx from the shell

1. Create a config file for your project, see the `sample/config.json`
2. Create a Figma personal access token
3. Run `export LOKALISE_TOKEN="your token"`
4. Run `lokex -c "path to your config`

## How to use LokEx as gradle plugin

1. Apply the plugin
```kotlin
plugins {
    apply("com.iodigital.lokex")
}
```
2. Configure the plugin
```kotlin
lokex {
  lokaliseToken = File("...").readText().trim()
  configFile = file("${configFile.absolutePath}")
}
```
3. Export using `./gradlew exportLokalise`

## Config file

- `exports`: Defines the exports to be done
  - `language`: The language to be exported
  - `platform`: One of `android`, `ios`, `web` or `other. Used to filter the strings.
  - `exportEmptyAs`: One of `base` (exports base language value) or `none` (skips value)
  - `destinationPath`: The file to which the generated code should be written
  - `templatePath`: The path for the template file
  - `projectId`: The project ID, may be multiple separated by comma (works similar to `includeId` of the Lokalise CLI)
  - `includeProjectId`: Optional, second project ID to include
  - `templateVariables`: A map of extra variables for the template. If you define `test` here you can later use `{{ test }}` in your template file

## Templating

The templating engine uses Jinja2 syntax. You can use loops, if statements and more.

### Available variables
- `strings`: A list of String objects
- `plural`: A list of Plurals objects
- `lokalise`: A figma file object
- `date`: The current date

#### String
- `name`: A name object
- `value`: The value of the string
- `placeholders`: A list of placeholder objects

#### Plurals
- `name`: A name object
- `variants`: A list of variant objects
- `placeholders`: A list of placeholder objects

#### Variant
- `quantity`: The quantity, e.g. `one` or `many`
- `value`: The value of the string

#### Placeholder
- `index`: The index of the placeholder
- `type`: The type of the placeholder: `Int`, `Double`, `String`, or `Char`

#### Name
Hint: You can also use Jinja filters to modify the name, e.g. `{{ color.name|lowercase|replace("some", "other") }}`

- `original`: The name as defined in Figma
- `snake`: The name in snake case
- `kebab`: The name in kebab case
- `pascal`: The name in pascal case


#### Lokalise
- `project_id`: The Lokalise project ID
- `language`: The language that was exported