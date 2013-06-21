window.jenkem_js_Cropper = function() {
	var mycropper = new croplib.Cropper(this.getElement());

	this.onStateChange = function() {
		mycropper.setImageSrc(this.getState().imageSrc);
	};

	var connector = this;
	mycropper.select = function(args) {
		connector.onSelect(args);
	};
};
