(function() {
        function dist2D(x1, y1, x2, y2) {
            var dx = x2 - x1;
            var dy = y2 - y1;
            return Math.sqrt(dx * dx + dy * dy);
        }
        CanvasRenderingContext2D.prototype.textOverflow = "";
        CanvasRenderingContext2D.prototype.textJustify = false;
        CanvasRenderingContext2D.prototype.textStrokeMin = 0;
        var state = [];
        var save = CanvasRenderingContext2D.prototype.save;
        CanvasRenderingContext2D.prototype.save = function() {
            state.push({
                textOverflow: this.textOverflow,
                textJustify: this.textJustify,
                textStrokeMin: this.textStrokeMin,
            });
            save.call(this);
        }
        var restore = CanvasRenderingContext2D.prototype.restore;
        CanvasRenderingContext2D.prototype.restore = function() {
            restore.call(this);
            var s = state.pop();
            this.textOverflow = s.textOverflow;
            this.textJustify = s.textJustify;
            this.textStrokeMin = s.textStrokeMin;
        }
        CanvasRenderingContext2D.prototype.textPath = function(text, path) {
            var di, dpos = 0;
            var pos = 2;
            function pointAt(dl) {
                if (!di || dpos + di < dl) {
                    for (; pos < path.length; ) {
                        di = dist2D(path[pos - 2], path[pos - 1], path[pos], path[pos + 1]);
                        if (dpos + di > dl)
                            break;
                        pos += 2;
                        if (pos >= path.length)
                            break;
                        dpos += di;
                    }
                }
                var x, y, dt = dl - dpos;
                if (pos >= path.length) {
                    pos = path.length - 2;
                }
                if (!dt) {
                    x = path[pos - 2];
                    y = path[pos - 1];
                } else {
                    x = path[pos - 2] + (path[pos] - path[pos - 2]) * dt / di;
                    y = path[pos - 1] + (path[pos + 1] - path[pos - 1]) * dt / di;
                }
                return [x, y, Math.atan2(path[pos + 1] - path[pos - 1], path[pos] - path[pos - 2])];
            }
            var letterPadding = this.measureText(" ").width * 0.25;
            var d = 0;
            for (var i = 2; i < path.length; i += 2) {
                d += dist2D(path[i - 2], path[i - 1], path[i], path[i + 1])
            }
            if (d < this.minWidth)
                return;
            var nbspace = text.split(" ").length - 1;
            if (this.textOverflow != "visible") {
                if (d < this.measureText(text).width + (text.length - 1 + nbspace) * letterPadding) {
                    var overflow = (this.textOverflow == "ellipsis") ? '\u2026' : this.textOverflow || "";
                    var dt = overflow.length - 1;
                    do {
                        if (text[text.length - 1] === " ")
                            nbspace--;
                        text = text.slice(0, -1);
                    } while (text && d < this.measureText(text + overflow).width + (text.length + dt + nbspace) * letterPadding)text += overflow;
                }
            }
            var start = 0;
            switch (this.textJustify || this.textAlign) {
                case true:
                case "center":
                case "end":
                case "right":
                {
                    if (this.textJustify) {
                        start = 0;
                        letterPadding = (d - this.measureText(text).width) / (text.length - 1 + nbspace);
                    } else {
                        start = d - this.measureText(text).width - (text.length + nbspace) * letterPadding;
                        if (this.textAlign == "center")
                            start /= 2;
                    }
                    break;
                }
                default:
                    break;
            }
            for (var t = 0; t < text.length; t++) {
                var letter = text[t];
                var wl = this.measureText(letter).width;
                var p = pointAt(start + wl / 2);
                this.save();
                this.textAlign = "center";
                this.translate(p[0], p[1]);
                this.rotate(p[2]);
                if (this.lineWidth > 0.1)
                    this.strokeText(letter, 0, 0);
                this.fillText(letter, 0, 0);
                this.restore();
                start += wl + letterPadding * (letter == " " ? 2 : 1);
            }
        }
        ;
    }
)();
