export class testing {
  public message: string = 'binding events does work';

  //font toggle
  private font:boolean = false;

  //text toggle
  toggleText() {
    let message = this.message;
    console.log('text-click registered');
    if(message == "binding events does work") {
      this.message = 'and changing text too';
    } else {
      this.message = 'binding events does work';
    }
  }

  // TS / tuple test
  x : [string, number] = ['test', 0];

  //elevation test
  e_items = Array.from(Array(25), (x, i) => i);

  //dessert test
  ice:dessert = new dessert('ice',5,50,5,'nice');
  Eclair:dessert = new dessert('Eclair',30,20,0,'meh');
  desserts: dessert[] =  [this.ice, this.Eclair] ;

  //Dialog tests
  handleClosing(text, isOK) {
    isOK?window.alert(text) : window.alert('notOK');
  }

  //Drawer test
  items: item[] = [
    { label: 'Inbox', icon: 'inbox', activated: true },
    { label: 'Star', icon: 'star', activated: false },
    { label: 'Sent Mail', icon: 'send', activated: false },
    { label: 'Drafts', icon: 'drafts', activated: false }
  ];
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

class item {
  // label: string;
  // icon: string;
  // activated: boolean;
}

