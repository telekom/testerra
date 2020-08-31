import {PLATFORM, autoinject} from "aurelia-framework";
import {Router, RouterConfiguration} from 'aurelia-router';
//import {DataBackendService} from "./services/data-backend-service";
import {png2bin} from "png2bin";
import {BSON} from 'bsonfy';
import * as base64 from "byte-base64";
import {bytesToBase64} from "byte-base64";
import {bin2png} from "bin2png";

@autoinject()
export class App {

  router: Router;
  _data: HTMLScriptElement;
  source: string;
  _dataImage: HTMLElement;
  _dataImage1: HTMLElement;

  //img = document.getElementById("myfile");
  constructor(
    // private _dataservice : DataBackendService
  ) {
    //_dataservice.getProject().then(value => console.log("Data: ",value))
    let doc = {Text: "hi"};
    let bson = BSON.serialize(doc);
    bin2png(bson).then(pngData => {
      const bs64 = bytesToBase64(pngData);
      this.source = "data:image/png;base64," + bs64;
      //console.log(bs64);
    })
    //png2bin(this.img).then(mydata => {
  }

  attached() {
    png2bin(this._dataImage).then(mydata => {
      let ram = BSON.deserialize(mydata)
      console.log("Data", ram);
    });

    png2bin(this._dataImage1).then(mydata => {
      console.log("DATA", mydata);
    });
  }


  configureRouter(config: RouterConfiguration, router: Router): void {
    this.router = router;
    config.title = 'Testerra Report';
    config.map([
      {
        route: '',
        name: 'Dashboard',
        moduleId: PLATFORM.moduleName('components/dashboard'),
        nav: true,
        title: 'Dashboard'
      },
      {
        route: 'classes',
        name: 'Classes',
        moduleId: PLATFORM.moduleName('components/classes'),
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


