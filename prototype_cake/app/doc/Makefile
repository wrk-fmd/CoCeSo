
all: api.pdf

api.pdf: api.tex
	@pdflatex api.tex
	@pdflatex api.tex

clean:
	@rm -f *~ *.bak
	@rm -f *.log *.aux *.toc
	@rm -f *.ilg *.idx *.ind
	@rm -f *.lot *.lof
	@rm -f *.blg *.bbl
	@rm -f *.nav *.out *.snm

clean-out: clean
	@rm -f *.dvi *.ps *.pdf
