export class Config {
    readonly enableServiceWorker = false;
    readonly developmentMode = true;
    correctRelativePath(path:string) {
        return path.replaceAll("\\","/").replace("test-report","");
    }
}
