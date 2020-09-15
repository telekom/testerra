import {data} from "services/report-model";
import ClassContext = data.ClassContext;
import MethodContext = data.MethodContext;
export class MergedClass{

  classContext:ClassContext;
  methodContexts:MethodContext[] = [];

  constructor(classContext:ClassContext) {
    this.classContext = classContext;
  }

  addMethodContext(methodContext:MethodContext) {
    this.methodContexts.push(methodContext);
  }

}
