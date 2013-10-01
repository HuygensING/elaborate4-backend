#MD = $(shell find src/main/docs -name '*.md')
#HTML = $(MD:.md=.html)

src/main/resources/static/%.html : src/main/docs/%.md
	markdown $< > $@

#all :
#	$(HTML)
