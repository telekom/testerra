import {data} from "../services/report-model";
import {DataLoader} from "../services/data-loader";
import {StatusConverter} from "../services/status-converter";
import {autoinject} from "aurelia-framework";
import ResultStatusType = data.ResultStatusType;

@autoinject()
export class Classes {
  message: string;
  _testsClasses: string;
  _testsPassed:number = 0;
  _testsSkipped:number =0
  _testsFailed: number= 0;

  constructor(
    private _dataLoader:DataLoader,
    private _statusConverter:StatusConverter

  ) {
    this.message = 'This is the classes component. Unfortunately I cannot bind hrefs to a button, which is why the a tag wraps around some other span';
  }
  attached() {
    this._dataLoader.getExecutionAggregate().then(executionAggregate => {
      console.log(executionAggregate);
      executionAggregate.testContexts.forEach(testContext => {
        testContext.classContextIds.forEach(classContextId => {
          this._dataLoader.getClassContextAggregate(classContextId).then(classContextAggregate => {
            console.log(classContextAggregate);

            classContextAggregate.methodContexts.forEach(methodContext => {
              const status = this._statusConverter.groupStatisticStatus(methodContext.contextValues.resultStatus);
              if (status == ResultStatusType.PASSED) {
                this._testsPassed++;
              }
              if(status==ResultStatusType.FAILED){
                this._testsFailed++;
              }
              if(status==ResultStatusType.SKIPPED){
                this._testsSkipped++;
              }
            });
            this._testsClasses= classContextAggregate.classContext.contextValues.name;

          });
        })
      });
    });

  }

  //dessert test
  ice:dessert = new dessert('ice',5,50,5,'nice');
  Eclair:dessert = new dessert('Eclair',30,20,0,'meh');
  desserts: dessert[] =  [this.ice, this.Eclair] ;

}
class dessert{
  name: string;
  calories: number;
  carbs: number;
  protein: number;
  comment: string;
  constructor(name, calories, carbs, protein, comment) {
    this.name =  name;
    this.calories = calories;
    this.carbs = carbs;
    this.protein = protein;
    this.comment = comment;
  }
}
