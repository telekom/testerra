# CONTRIBUTING to Testerra

## Bug Reports
Before opening a new bug in our GitHub issue tracker, please have a look at the open issues to avoid duplicates as early as possible.

If you don't find an open or - even better - already closed issue addressing your problem, please feel free to create an issue providing at least the information that will be asked in the issue template.

To give your bug more credit you may add a testcase addressing your problem to make it easier for us to reproduce your error. But, please don't worry if you don't. 

Remember that bug reports should be able to reproduce and allow other members of the community to collaborate on it.
Please do not expect that the bug will automatically see any activity to fix it. Creating a bug just is the start of the path of fixing a problem. 

## Support Questions
Currently we support any kind of issues in our GitHub issue tracker. Please open a new issue, after checking for already present duplicates. 

Take a look at the given template for issues of type `support question` to make things as clear as possible.

## Improvements and Feature Requests
We're open to any new feature requests or improvements of existing Testerra behavior. 
If you want to propose a new feature, please just create a new issue in our GitHub issue tracker, but please be willing to implement at least some code for the new feature.

### How To
- Fork the Testerra repository
- Clone your forked repository
- Create a new branch in your cloned repository to represent your changes
- Add your code, comments, testcases and update the `CHANGES.md` file with a small comment addressing
- Add commit messages that provide benefit and add the GitHub issue number to it
- Push your code to your forked repository

### Which branch?

All *bug fixes* should be sent to the latest stable branch because they are fixing actual behaviour.
We will then take care of merging it to other branches as well.

*Fully backwards compatible* (minor) features / improvements should be sent to the latest stable branch.

New *major* features should always be sent to the `development` branch, which contains all work for the next release. 

## Coding Style
Try to match our present coding style as well as possible. He're some facts about it:
* Spaces / Indents: 4 spaces - NO tabs
* No wildcard imports
* TBD...

Please be not offended personally, when we decline a pull request that won't match our coding style. 
We just want to ensure that any contributor is able to hop in your code and is able to code in a defined way.
