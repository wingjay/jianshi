import webbrowser

from server.data import images


def execute():
    file_path = "server/data/preview.html"
    open(file_path, 'w').close()
    fo = open(file_path, "r+")
    print "name: ", fo.name
    print fo.read()
    fo.flush()
    fo.write("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><title>Image Preview</title></head><body>")
    jianshi_image = images.images + images.christmas + images.people + images.winter + images.sports
    for image in jianshi_image:
        image_tag = "<img src='%s?w=400' width=400>" + image + "</img><br>"
        fo.write(image_tag % image)
    fo.write('</body></html>')
    fo.close()
    # MacOS
    chrome_path = 'open -a /Applications/Google\ Chrome.app %s'
    webbrowser.get(chrome_path).open(file_path)
