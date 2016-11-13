from server.data import images


def execute():
    fo = open("server/data/preview.html", "r+")
    print "name: ", fo.name
    print fo.read()
    fo.flush()
    fo.write("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><title>Image Preview</title></head><body>")
    image_tag = "<img src='%s?w=400' width=400/>"
    for image in images.images:
        fo.write(image_tag % image)
    fo.write('</body></html>')
    fo.close()
