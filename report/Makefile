TEXFILES=munksgaard-philip-report.tex

DOTFILES=

DOT_PS=$(DOT:.dot=.ps)
DOT_EPSI=$(DOT:.dot=.epsi)

FIGURES=$(DOTFILES:.dot=.epsi)

$(TEXFILES:.tex=.pdf): %.pdf: %.ps
	ps2pdf $<

$(TEXFILES:.tex=.ps): %.ps: %.dvi
	dvips $<

$(TEXFILES:.tex=.dvi): %.dvi: %.tex $(FIGURES)
	latex $<
	latex $<
	latex $<

$(DOTFILES:.dot=.epsi): %.epsi: %.ps
	ps2epsi $<

$(DOTFILES:.dot=.ps): %.ps: %.dot
	dot -Tps $< -o$@

clean:
	rm -f *.ps *.out *.log *.aux *.epsi *.eps *.dvi $(TEXFILES:.tex=.pdf)
