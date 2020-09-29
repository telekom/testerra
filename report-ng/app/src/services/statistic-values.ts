import {autoinject} from "aurelia-framework";
import {DataBackendService} from "./data-backend-service";
import {MergedClass} from "./merged-class";

@autoinject()
export class StatisticValues {
  private mergedClasses: MergedClass[];
  constructor(
    private _dataservice: DataBackendService,
  ) {

  }
  outputClasses() {
    for(let mergedClass of this.mergedClasses) {
      console.log(mergedClass);
    }
  }
  createMergeClassStatistics() : any {

    // this._dataservice.getExecution().then(executionContext => {
    //   console.log(executionContext);
    //   for (let suiteContextId of executionContext.suiteContextIds) {
    //     this._dataservice.getSuite(suiteContextId).then(suiteContext => {
    //       for (let testContextId of suiteContext.testContextIds) {
    //         this._dataservice.getTest(testContextId).then(testContext => {
    //           for (let classContextId of testContext.classContextIds) {
    //             this._dataservice.getClass(classContextId).then(classContext => {
    //               const mergedClass = new MergedClass(classContext);
    //               for (let methodContextId of classContext.methodContextIds) {
    //                 this._dataservice.getMethod(methodContextId).then(methodContext => {
    //                   mergedClass.addMethodContext(methodContext);
    //                 })
    //               }
    //               this.mergedClasses.push(mergedClass);
    //               return this.mergedClasses;
    //             })
    //           }
    //         })
    //       }
    //     })
    //   }
    // })
  }
}
