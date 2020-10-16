import {autoinject, PLATFORM} from "aurelia-framework";
import {Router, RouterConfiguration} from 'aurelia-router';
import {DataLoader} from "./services/data-loader";
import {StatusConverter} from "./services/status-converter";

@autoinject()
export class App {

  router: Router;
  private  _logo = "../static/logo.png"; //workaround for webpack changing filename and therefor messing up the src-attribute

  constructor(
    private _dataLoader : DataLoader,
    private _statusConverter : StatusConverter
  ) {

  }

  attached() {

  }


  configureRouter(config: RouterConfiguration, router: Router): void {
    this.router = router;
    config.title = 'Testerra Report';
    config.map([
      {
        route: '',
        name: 'Dashboard',
        moduleId: PLATFORM.moduleName('components/dashboard/dashboard'),
        nav: true,
        title: 'Dashboard'
      },
      {
        route: 'classes',
        name: 'Classes',
        moduleId: PLATFORM.moduleName('components/classes/classes'),
        nav: true,
        title: 'Classes'
      },
      {
        route: 'threads',
        name: 'Threads',
        moduleId: PLATFORM.moduleName('components/threads'),
        nav: true,
        title: 'Threads'
      },
      {
        route: 'exit-points',
        name: 'Exit Points',
        moduleId: PLATFORM.moduleName('components/exit'),
        nav: true,
        title: 'Exit Points'
      },
      {route: 'logs', name: 'Logs', moduleId: PLATFORM.moduleName('components/logs'), nav: true, title: 'Logs'},
      {
        route: 'timings',
        name: 'Timings',
        moduleId: PLATFORM.moduleName('components/timings'),
        nav: true,
        title: 'Timings'
      },
      {
        route: 'jvm-monitor',
        name: 'JVM Monitor',
        moduleId: PLATFORM.moduleName('components/jvm'),
        nav: true,
        title: 'JVM Monitor'
      },
      {
        route: 'testing',
        name: 'Testing',
        moduleId: PLATFORM.moduleName('components/testing'),
        nav: true,
        title: 'Testing'
      },

    ]);
  }

  navigateTo(route) {
    this.router.navigateToRoute(route)
  }
}


