export class Config {
    readonly enableServiceWorker = false;
    readonly developmentMode = true;
    correctRelativePath(path:string) {
        return path.replace("\\","/").replace("test-report","");
    }
}
