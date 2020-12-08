export class Config {
    readonly enableServiceWorker = false;
    readonly developmentMode = true;
    private pathRegex = new RegExp("[^\\/]+\\/")
    correctRelativePath(path:string) {
        return path.replaceAll("\\","/").replace(this.pathRegex,"");
    }
}
