var MINOVATE = MINOVATE || {};

$(function() {




  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  // global inicialization functions
  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

  MINOVATE.global = {

    init: function() {
      MINOVATE.global.deviceSize();
      MINOVATE.global.goToTop();
      //MINOVATE.global.animsition();
    },

    // device identification function
    deviceSize: function(){
			var jRes = jRespond([
				{
					label: 'smallest',
					enter: 0,
					exit: 479
				},{
					label: 'handheld',
					enter: 480,
					exit: 767
				},{
					label: 'tablet',
					enter: 768,
					exit: 991
				},{
					label: 'laptop',
					enter: 992,
					exit: 1199
				},{
					label: 'desktop',
					enter: 1200,
					exit: 10000
				}
			]);
			jRes.addFunc([
				{
					breakpoint: 'desktop',
					enter: function() { $body.addClass('device-lg'); },
					exit: function() { $body.removeClass('device-lg'); }
				},{
					breakpoint: 'laptop',
					enter: function() { $body.addClass('device-md'); },
					exit: function() { $body.removeClass('device-md'); }
				},{
					breakpoint: 'tablet',
					enter: function() { $body.addClass('device-sm'); },
					exit: function() { $body.removeClass('device-sm'); }
				},{
					breakpoint: 'handheld',
					enter: function() { $body.addClass('device-xs'); },
					exit: function() { $body.removeClass('device-xs'); }
				},{
					breakpoint: 'smallest',
					enter: function() { $body.addClass('device-xxs'); },
					exit: function() { $body.removeClass('device-xxs'); }
				}
			]);
		},

    // scroll to top
    goToTop: function(){
			$goToTopEl.click(function() {
				$('body,html').stop(true).animate({scrollTop:0},400);
				return false;
			});
		},

		// show gotoTop element
    goToTopScroll: function(){
			if( $body.hasClass('device-lg') || $body.hasClass('device-md') || $body.hasClass('device-sm') ) {
				if($window.scrollTop() > 450) {
					$goToTopEl.fadeIn();
				} else {
					$goToTopEl.fadeOut();
				}
			}
		},

    // initialize animsition

    animsition: function() {
      $wrapper.animsition({
        inClass               :   'fade-in',
        outClass              :   'fade-out',
        inDuration            :    1500,
        outDuration           :    800,
        linkElement           :   '.animsition-link',
        // e.g. linkElement   :   'a:not([target="_blank"]):not([href^=#])'
        loading               :    true,
        loadingParentElement  :   'body', //animsition wrapper element
        loadingClass          :   'animsition-loading',
        unSupportCss          : [ 'animation-duration',
          '-webkit-animation-duration',
          '-o-animation-duration'
        ],
        //"unSupportCss" option allows you to disable the "animsition" in case the css property in the array is not supported by your browser.
        //The default setting is to disable the "animsition" in a browser that does not support "animation-duration".

        overlay               :   false,
        overlayClass          :   'animsition-overlay-slide',
        overlayParentElement  :   'body'
      });
    }

  };






  //!!!!!!!!!!!!!!!!!!!!!!!!!
  // header section functions
  //!!!!!!!!!!!!!!!!!!!!!!!!!

  MINOVATE.header = {

    init: function() {
      MINOVATE.header.superfish();
      MINOVATE.header.megaMenuWidth();
      MINOVATE.header.searchToggle();
      MINOVATE.header.chooseMenuStyle();
      MINOVATE.header.chooseLogo();
      MINOVATE.header.stickyHeader();
      MINOVATE.header.menuClasses();
      MINOVATE.header.toggleMenu();
    },


    //initialize superfish on main-navbar
    superfish: function(){

      if( $body.hasClass('device-lg') || $body.hasClass('device-md') ) {
        $('#main-navbar ul ul, #main-navbar ul .mega-menu-content').css('display', 'block');
			  MINOVATE.header.dropdownPosition();
      }

			$('#main-navbar > ul, #main-navbar > div > ul').superfish({
        popUpSelector: 'ul,.mega-menu-content',
        delay: 250,
        speed: 350,
        animation: {opacity:'show'},
        animationOut:  {opacity:'hide'},
        cssArrows: false
      });

		},

    //choose side for dropdown menu open
    dropdownPosition: function() {

			$('#main-navbar .mega-menu-content, #main-navbar ul ul').each( function( index, element ){
				var $dropdown = $(element);
				var windowWidth = $window.width();
				var dropdownOffset = $dropdown.offset();
				var dropdownWidth = $dropdown.width();
				var dropdownPosition = dropdownOffset.left;

				if(windowWidth - (dropdownWidth + dropdownPosition) < 0) {
					$dropdown.addClass('menu-right-position');
				}
			});

		},

    //set width of mega-menu
    megaMenuWidth: function(){

      $('.mega-menu .mega-menu-content').css({
        'width': $wrapper.width() - 60
      });

    },

    //toggle search box
    searchToggle: function(){

      $('#search-toggle a').click(function(e){
        e.preventDefault();

        if($searchBoxWrapper.height() == "0"){

          $('html, body').animate({
            scrollTop: 0
          }, 300, false);

          $('.search-box-wrapper input[type="search"]').focus();

          $searchBoxWrapper.animate({
            height:"50px"
          }, 300);

        } else {

          $searchBoxWrapper.animate({
            height:"0"
          }, 300);

        }

      });

    },

    // choose if logo need to be light or dark
    chooseLogo: function(){
			if($header.hasClass('light')) {
				if( defaultLightLogo ){ defaultLogo.find('img').attr('src', defaultLightLogo); }
				if( retinaLightLogo ){ retinaLogo.find('img').attr('src', retinaLightLogo); }
			} else {
				if( defaultLogoImg ){ defaultLogo.find('img').attr('src', defaultLogoImg); }
				if( retinaLogoImg ){ retinaLogo.find('img').attr('src', retinaLogoImg); }
			}
		},

    // makes header sticky
    stickyHeader: function( headerOffset ){
			if ($window.scrollTop() > headerOffset) {
				if( ( $body.hasClass('device-lg') || $body.hasClass('device-md') ) && !MINOVATE.isMobile.any() ) {
					$header.addClass('sticky-header');
					$header.removeClass('light');
				} else if( $body.hasClass('device-xs') || $body.hasClass('device-xxs') || $body.hasClass('device-sm') ) {
          if( $header.hasClass('sticky-mobile') ) { $header.addClass('responsive-sticky-header'); }
				} else {
					MINOVATE.header.unStickyHeader();
				}
			} else {
				MINOVATE.header.unStickyHeader();
			}
		},

    // remove sticky class on header
    unStickyHeader: function(){
			if( $header.hasClass('sticky-header') ){
				$header.removeClass('sticky-header');
				$header.removeClass().addClass(oldHeaderClasses);
			}
			if( $header.hasClass('responsive-sticky-header') ){
				$header.removeClass('responsive-sticky-header');
			}
		},

    // class toggling on menu according to windows width
    chooseMenuStyle: function(headerOffset) {
      if( $body.hasClass('device-xs') || $body.hasClass('device-xxs') || $body.hasClass('device-sm')) {
        $header.removeClass('light');
        if( !$header.hasClass('sticky-mobile') ) { MINOVATE.header.unStickyHeader(); }
      } else {
        if( $window.scrollTop() > headerOffset ) { $header.addClass('sticky-header'); }
        if( !$header.hasClass('sticky-header')) { $header.addClass('light'); }
        if( $header.hasClass('dark')) { $header.removeClass('light'); }
      }
    },

    // add sub-menu class to determined menu items
    menuClasses: function() {
      $( '#main-navbar ul li:has(ul)' ).addClass('submenu');

      if( MINOVATE.isMobile.Android() ) {
				$( '#main-navbar ul li.sub-menu' ).children('a').on('touchstart', function(e){
					if( !$(this).parent('li.submenu').hasClass('sfHover') ) {
						e.preventDefault();
					}
				});
			}
    },

    // toggling display of menu
    toggleMenu: function() {
      $menuToggler.click(function() {
				$( '#main-navbar > ul' ).toggleClass("show");
				return false;
			});
    }


  };
  
  
  //!!!!!!!!!!!!!!!!!!!!!!!!!
  // slider section functions
  //!!!!!!!!!!!!!!!!!!!!!!!!!
  
  MINOVATE.slider = {

		init: function() {

			MINOVATE.slider.sliderParallax();

		},

		//set parallax offset
    sliderParallaxOffset: function(){
			var sliderParallaxOffsetTop = 0;
			var headerHeight = $header.outerHeight();
			if( $body.hasClass('side-header') || $header.hasClass('transparent-header') ) { headerHeight = 0; }
			if( $pageTitle.length > 0 ) {
				var pageTitleHeight = $pageTitle.outerHeight();
				sliderParallaxOffsetTop = pageTitleHeight + headerHeight;
			} else {
				sliderParallaxOffsetTop = headerHeight;
			}

			if( $slider.next('#header').length > 0 ) { sliderParallaxOffsetTop = 0; }

			return sliderParallaxOffsetTop;
		},

		//parallax transform function
    sliderParallax: function(){
			if( ( $body.hasClass('device-lg') || $body.hasClass('device-md') ) && !MINOVATE.isMobile.any() ) {
				var parallaxOffsetTop = MINOVATE.slider.sliderParallaxOffset();
				if ($window.scrollTop() > parallaxOffsetTop) {
					$sliderParallaxEl.css({ 'transform' : 'translate(0,'+ (($window.scrollTop()-parallaxOffsetTop) / 1.5 ) +'px)' });
				} else {
					//$('.slider-parallax').css({ 'transform' : 'translate(0,0)' });
				}
			} else {
				$('.slider-parallax').css({ 'transform' : 'translate(0,0)' });
			}
		}

		
	};





  //!!!!!!!!!!!!!!!!
  // extra functions
  //!!!!!!!!!!!!!!!!

  MINOVATE.extra = {

    init: function() {
      MINOVATE.extra.animations();
      MINOVATE.extra.parallax();
      MINOVATE.extra.loadFlexSlider();
      MINOVATE.extra.html5Video();
      MINOVATE.extra.counter();
      MINOVATE.extra.progress();
      MINOVATE.extra.mixitup();
    },


    //initialize animations on elements
    animations: function(){

      var $animateEl = $('[data-animate]');

      if( $animateEl.length > 0 ){

        if( $body.hasClass('device-lg') || $body.hasClass('device-md') || $body.hasClass('device-sm') ){

          $animateEl.each(function(){
            var el = $(this),
							  delay = el.attr('data-delay'),
							  delayTime = 0;

						if( delay ) { delayTime = Number( delay ) + 500; } else { delayTime = 500; }

						if( !el.hasClass('animated') ) {
							el.addClass('not-animated');
							var elAnimation = el.attr('data-animate');
							el.appear(function () {
								setTimeout(function() {
									el.removeClass('not-animated').addClass( elAnimation + ' animated');
								}, delayTime);
							},{accX: 0, accY: -120},'easeInCubic');
						}

					});

				}

			}

		},

    // initialize parallax effect on parallax elements
    parallax: function(){
			if( !MINOVATE.isMobile.any() ){
				$.stellar({
					horizontalScrolling: false,
					verticalOffset: 150,
					responsive: true
				});
			}
		},

    // initialize flexslider
    loadFlexSlider: function(){
			var $flexSliderEl = $('.fslider').find('.flexslider');

			if( $flexSliderEl.length > 0 ){
				$flexSliderEl.each(function() {
					var $flexsSlider = $(this),
              flexsAnimation = $flexsSlider.parent('.fslider').attr('data-animation'),
              flexsEasing = $flexsSlider.parent('.fslider').attr('data-easing'),
              flexsDirection = $flexsSlider.parent('.fslider').attr('data-direction'),
              flexsSlideshow = $flexsSlider.parent('.fslider').attr('data-slideshow'),
              flexsPause = $flexsSlider.parent('.fslider').attr('data-pause'),
              flexsSpeed = $flexsSlider.parent('.fslider').attr('data-speed'),
              flexsVideo = $flexsSlider.parent('.fslider').attr('data-video'),
              flexsPagi = $flexsSlider.parent('.fslider').attr('data-pagi'),
              flexsArrows = $flexsSlider.parent('.fslider').attr('data-arrows'),
              flexsThumbs = $flexsSlider.parent('.fslider').attr('data-thumbs'),
              flexsSheight = true,
              flexsUseCSS = false;

					if( !flexsAnimation ) { flexsAnimation = 'slide'; }
					if( !flexsEasing || flexsEasing == 'swing' ) {
						flexsEasing = 'swing';
						flexsUseCSS = true;
					}
					if( !flexsDirection ) { flexsDirection = 'horizontal'; }
					if( !flexsSlideshow ) { flexsSlideshow = true; } else { flexsSlideshow = false; }
					if( !flexsPause ) { flexsPause = 5000; }
					if( !flexsSpeed ) { flexsSpeed = 600; }
					if( !flexsVideo ) { flexsVideo = false; }
					if( flexsDirection == 'vertical' ) { flexsSheight = false; }
					if( flexsPagi == 'false' ) { flexsPagi = false; } else { flexsPagi = true; }
					if( flexsThumbs == 'true' ) { flexsPagi = 'thumbnails'; } else { flexsPagi = flexsPagi; }
					if( flexsArrows == 'false' ) { flexsArrows = false; } else { flexsArrows = true; }

					$flexsSlider.flexslider({
						selector: ".slider-wrap > .slide",
						animation: flexsAnimation,
						easing: flexsEasing,
						direction: flexsDirection,
						slideshow: flexsSlideshow,
						slideshowSpeed: Number(flexsPause),
						animationSpeed: Number(flexsSpeed),
						pauseOnHover: true,
						video: flexsVideo,
						controlNav: flexsPagi,
						directionNav: flexsArrows,
						smoothHeight: flexsSheight,
						useCSS: flexsUseCSS,
						start: function(slider){
							$('.flex-prev').html('<i class="fa fa-angle-left"></i>');
							$('.flex-next').html('<i class="fa fa-angle-right"></i>');
						}
					});
				});
			}
		},



    //initialize resizing
    html5Video: function() {
      if ($body.hasClass('device-sm') || $body.hasClass('device-xs') || $body.hasClass('device-xxs')) {
        $('.section:has(video), .parallax-box:has(video)').each(function () {
          var outerContainerWidth = $(this).outerWidth();
          var outerContainerHeight = $(this).outerHeight();
          var innerVideoWidth = $(this).find('video').outerWidth();
          var innerVideoHeight = $(this).find('video').outerHeight();
          if (innerVideoHeight < outerContainerHeight) {
            var videoAspectRatio = innerVideoWidth / innerVideoHeight;
            var newVideoWidth = outerContainerHeight * videoAspectRatio;
            var innerVideoPosition = (newVideoWidth - outerContainerWidth) / 2;
            $(this).find('video').css({
              'width': newVideoWidth + 'px',
              'height': outerContainerHeight + 'px',
              'left': -innerVideoPosition + 'px',
              'position': 'relative'
            });
          } else {
            var innerVideoPosition = (innerVideoHeight - outerContainerHeight) / 2;
            $(this).find('video').css({
              'width': innerVideoWidth + 'px',
              'height': innerVideoHeight + 'px',
              'top': -innerVideoPosition + 'px',
              'position': 'relative'
            });
          }
        });
      } else {
        $('.section:has(video), .parallax-box:has(video)').each(function () {
          $(this).find('video').css({
            'width': '100%',
            'height': 'auto',
            'position': 'static'
          });
        });
      }
    },

    //initialize countTo
    counter: function(){
      var $counterEl = $('.counter:not(.counter-instant)');
			if( $counterEl.length > 0 ){
				$counterEl.each(function(){
					var element = $(this);
          element.appear( function(){
            MINOVATE.extra.runCounter(element);
          });
				});
			}
    },

    //run countTo
    runCounter: function( counterElement){
			counterElement.find('span').countTo();
		},

    //animate progress bar
    progress: function(){
			var $progressEl = $('.progress');
			if( $progressEl.length > 0 ){
				$progressEl.each(function(){
					var progressBar = $(this),
						  progressValue = progressBar.attr('data-percent');

					if( $body.hasClass('device-lg') || $body.hasClass('device-md') ){
						progressBar.appear( function(){
							if (!progressBar.hasClass('progress-animated')) {
								progressBar.find('.counter-instant span').countTo();
								progressBar.find('.progress-bar').css({width: progressValue + "%"}).addClass('progress-animated');
							}
						},{accX: 0, accY: -120},'easeInCubic');
					} else {
						progressBar.find('.counter-instant span').countTo();
						progressBar.find('.progress').css({width: progressValue + "%"});
					}
				});
			}
		},

    //initialize mixitup

    mixitup: function(){
			if( $mixitupEl.length > 0 ) {
				$mixitupEl.each(function() {
					var element = $(this);

					element.mixItUp();
				});
			}
		}

  };




  //!!!!!!!!!!!!!!!!!!!!
  // check mobile device
  //!!!!!!!!!!!!!!!!!!!!

  MINOVATE.isMobile = {
    Android: function() {
      return navigator.userAgent.match(/Android/i);
    },
    BlackBerry: function() {
      return navigator.userAgent.match(/BlackBerry/i);
    },
    iOS: function() {
      return navigator.userAgent.match(/iPhone|iPad|iPod/i);
    },
    Opera: function() {
      return navigator.userAgent.match(/Opera Mini/i);
    },
    Windows: function() {
      return navigator.userAgent.match(/IEMobile/i);
    },
    any: function() {
      return (MINOVATE.isMobile.Android() || MINOVATE.isMobile.BlackBerry() || MINOVATE.isMobile.iOS() || MINOVATE.isMobile.Opera() || MINOVATE.isMobile.Windows());
    }
  };



  //!!!!!!!!!!!!!!!!!!!!!!!!!
  // initialize after resize
  //!!!!!!!!!!!!!!!!!!!!!!!!!

  MINOVATE.documentOnResize = {

		init: function(){

      var headerWrapOffset = $headerWrap.offset().top;

      var t = setTimeout( function(){
				MINOVATE.header.megaMenuWidth();
        MINOVATE.header.chooseMenuStyle(headerWrapOffset);
        MINOVATE.header.chooseLogo();
        MINOVATE.extra.html5Video();
			}, 500 );

		}

	};


 //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  // initialize when document ready
  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

  MINOVATE.documentOnReady = {

		init: function(){
			$body = $('body'),
      $searchBoxWrapper = $('.search-box-wrapper'),
      $wrapper = $('#wrapper'),
      $header = $('#header'),
      $headerWrap = $('#header-wrap'),
      oldHeaderClasses = $header.attr('class'),
      defaultLogo = $('#branding').find('.brand-normal'),
      defaultLogoWidth = defaultLogo.find('img').outerWidth(),
      retinaLogo = $('#branding').find('.brand-retina'),
      defaultLogoImg = defaultLogo.find('img').attr('src'),
      retinaLogoImg = retinaLogo.find('img').attr('src'),
      defaultLightLogo = defaultLogo.attr('data-light-logo'),
      retinaLightLogo = retinaLogo.attr('data-light-logo'),
      $pageTitle = $('#page-title'),
      $slider = $('#slider'),
		  $sliderParallaxEl = $('.slider-parallax'),
      $menuToggler = $('#main-navbar-toggle'),
      $owlCarouselEl = $('.owl-carousel'),
      $goToTopEl = $('#gotoTop'),
      $mixitupEl = $('.mix-grid')
      MINOVATE.global.init();
			MINOVATE.header.init();
      MINOVATE.documentOnReady.windowscroll();
      MINOVATE.extra.init();
		},



 

    // run on window scrolling

    windowscroll: function(){

			var headerOffset = $header.offset().top;
			var headerWrapOffset = $headerWrap.offset().top;

			$window.on( 'scroll', function(){

				MINOVATE.global.goToTopScroll();
        MINOVATE.header.stickyHeader(headerWrapOffset);
				MINOVATE.header.chooseLogo();
        MINOVATE.slider.sliderParallax();

			});
		}

	};


  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  // initialize when document load
  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	MINOVATE.documentOnLoad = {

		init: function(){

		}

	};






  //!!!!!!!!!!!!!!!!!!!!!!!!!
  // global variables
  //!!!!!!!!!!!!!!!!!!!!!!!!!

  var $window = $(window),
      $body = $('body'),
      $searchBoxWrapper = $('.search-box-wrapper'),
      $wrapper = $('#wrapper'),
      $header = $('#header'),
      $headerWrap = $('#header-wrap'),
      oldHeaderClasses = $header.attr('class'),
      defaultLogo = $('#branding').find('.brand-normal'),
      defaultLogoWidth = defaultLogo.find('img').outerWidth(),
      retinaLogo = $('#branding').find('.brand-retina'),
      defaultLogoImg = defaultLogo.find('img').attr('src'),
      retinaLogoImg = retinaLogo.find('img').attr('src'),
      defaultLightLogo = defaultLogo.attr('data-light-logo'),
      retinaLightLogo = retinaLogo.attr('data-light-logo'),
      $pageTitle = $('#page-title'),
      $slider = $('#slider'),
		  $sliderParallaxEl = $('.slider-parallax'),
      $menuToggler = $('#main-navbar-toggle'),
      $owlCarouselEl = $('.owl-carousel'),
      $goToTopEl = $('#gotoTop'),
      $mixitupEl = $('.mix-grid');


});

			var apiRevoSlider = $('.tp-banner').show().revolution(
                                {
                                    dottedOverlay:"none",
                                    delay:9000,
                                    startwidth:1140,
                                    startheight:700,
                                    hideThumbs:200,

                                    thumbWidth:100,
                                    thumbHeight:50,
                                    thumbAmount:3,

                                    navigationType:"none",
                                    navigationArrows:"solo",
                                    navigationStyle:"preview1",

                                    touchenabled:"on",
                                    onHoverStop:"on",

                                    swipe_velocity: 0.7,
                                    swipe_min_touches: 1,
                                    swipe_max_touches: 1,
                                    drag_block_vertical: false,


                                    parallax:"mouse",
                                    parallaxBgFreeze:"on",
                                    parallaxLevels:[8,7,6,5,4,3,2,1],
                                    parallaxDisableOnMobile:"on",


                                    keyboardNavigation:"on",

                                    navigationHAlign:"center",
                                    navigationVAlign:"bottom",
                                    navigationHOffset:0,
                                    navigationVOffset:20,

                                    soloArrowLeftHalign:"left",
                                    soloArrowLeftValign:"center",
                                    soloArrowLeftHOffset:20,
                                    soloArrowLeftVOffset:0,

                                    soloArrowRightHalign:"right",
                                    soloArrowRightValign:"center",
                                    soloArrowRightHOffset:20,
                                    soloArrowRightVOffset:0,

                                    shadow:0,
                                    fullWidth:"off",
                                    fullScreen:"on",

                                    spinner:"spinner3",

                                    stopLoop:"off",
                                    stopAfterLoops:-1,
                                    stopAtSlide:-1,

                                    shuffle:"off",

                                    forceFullWidth:"off",
                                    fullScreenAlignForce:"off",
                                    minFullScreenHeight:"400",

                                    hideThumbsOnMobile:"off",
                                    hideNavDelayOnMobile:1500,
                                    hideBulletsOnMobile:"off",
                                    hideArrowsOnMobile:"off",
                                    hideThumbsUnderResolution:0,

                                    hideSliderAtLimit:0,
                                    hideCaptionAtLimit:0,
                                    hideAllCaptionAtLilmit:0,
                                    startWithSlide:0,
                                    fullScreenOffsetContainer: ".header"
                                });

                        apiRevoSlider.bind("revolution.slide.onchange",function (e,data) {
                            if( $(window).width() > 992 ) {
                                if( $('#slider ul > li').eq(data.slideIndex-1).hasClass('light') ){
                                    $('#header:not(.sticky-header)').addClass('light');
                                } else {
                                    $('#header:not(.sticky-header)').removeClass('light');
                                }
                                MINOVATE.header.chooseLogo();
                            }
                        });
