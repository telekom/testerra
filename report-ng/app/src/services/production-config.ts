export class Config {
    readonly enableServiceWorker = true;
    readonly developmentMode = false;
    correctRelativePath(path:string) {
        return "../../"+path.replace("\\","/");
    }
}
