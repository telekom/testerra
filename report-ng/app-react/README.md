# Testerra ReportNG with React

The navigation footer displays the Testerra version used to create the report and links to its documentation.
For SNAPSHOT versions, it also displays the build timestamp, matching the legacy report.

Install deps
````shell
bun install
````

Start dev
````shell
bun dev
````

Build for prod
````shell
npm run build

# or
bunx --bun vite build
````

## Add local testdata

* Create a new folder `public` in the root of the project
* Execute a test
* From the created report copy the `model`, `screenshot` and `video` folders into the `public` folder

