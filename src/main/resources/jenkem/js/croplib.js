var croplib = croplib || {};

croplib.Cropper = function(element) {
	function loadScript(url, callback) {
		var script = document.createElement('script');
		script.src = url;
		script.onreadystatechange = callback;
		script.onload = callback;
		document.getElementsByTagName('head')[0].appendChild(script);
	}

	function appendCss(url, callback) {
		var link = document.createElement('link');
		link.rel = 'stylesheet';
		link.type = 'text/css';
		link.href = url;
		link.onreadystatechange = callback;
		link.onload = callback;
		document.getElementsByTagName('head')[0].appendChild(link);
	}

	var defaultWidth = 100; //136;

	var component = this;
	this.element = element;
	this.element.style.display = "inline-block";
	this.element.innerHTML = "<img id='target' style='width: " + defaultWidth + "px; height: auto' />";

	this.select = function() {
		alert("Error: Must implement select() method");
	};

	appendCss("./VAADIN/jCrop/css/jquery.Jcrop.css", function() {
		loadScript("./VAADIN/jCrop/js/jquery.min.js", function() {
			loadScript("./VAADIN/jCrop/js/jquery.Jcrop.js", function() {
				jcropInit();
			});
		});
	});

	function updateCoordinates(c) {
		component.select(createArgs(c));
	}

	function createArgs(c) {
		var image = element.getElementsByTagName("img")[0];
		var width = parseInt(image.style.width, 10);
		var height = parseInt(image.style.height, 10);
		var args = [];
		args.push(c.x * 100 / width);
		args.push(c.y * 100 / height);
		args.push(c.x2 * 100 / width);
		args.push(c.y2 * 100 / height);
		args.push(c.w * 100 / width);
		args.push(c.h * 100 / height);
		return args;
	}

	var isReady = false;
	var jcrop_api;
	function jcropInit() {
		jQuery(function($) {
			$('#target').Jcrop({
				onSelect : updateCoordinates,
				boxWidth : defaultWidth,
				minSize : [ 32, 32 ]
			}, function() {
				jcrop_api = this;
				isReady = true;
				selectAll();
			});
		});
	}

	function addImage(imageSrc) {
		element.innerHTML = "<img src='" + imageSrc + "' id='target'/>";
		var image = element.getElementsByTagName("img")[0];
		image.style.width = defaultWidth + "px";
		image.style.height = "auto";
	}

	function changeSrc(imageSrc) {
		if (isReady) {
			jcrop_api.destroy();
			addImage(imageSrc);
			jcropInit();
		} else {
			setTimeout(changeSrc, 500, imageSrc);
		}
	}

	this.setImageSrc = function(imageSrc) {
		changeSrc(imageSrc);
	};

	function selectAll() {
		var image = element.getElementsByTagName("img")[0];
		var width = parseInt(image.style.width, 10);
		var height = parseInt(image.style.height, 10);
		jcrop_api.animateTo([ 0, 0, width, height ]);
		var args = [];
		args.push(0);
		args.push(0);
		args.push(100);
		args.push(100);
		args.push(100);
		args.push(100);
		component.select(args);
	}
};
