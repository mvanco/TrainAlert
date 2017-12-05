package cz.intesys.trainalert.animation;

//public class TrainAnimator {
//
//    GeoPoint lastLocation;
//    ValueAnimator valueAnimator;
//
//    public TrainAnimator(GeoPoint initialLocation) {
//        valueAnimator = new ValueAnimator();
//    }
//
//    public void animateToPosition(GeoPoint destinationLocation) {
//        valueAnimator.addUpdateListener(new TrainAnimator.AnimatedFractionHandler(destinationLocation));
//        valueAnimator.setFloatValues(0, 1); // Ignored.
//        valueAnimator.setDuration(3000);
//        valueAnimator.start();
//    }
//
//
//    private class AnimatedFractionHandler implements ValueAnimator.AnimatorUpdateListener {
//
//        private GeoPoint lastLocation;
//        private final GeoPointInterpolator interpolator = null;
//        //private Integer animatedValue;
//
//
//        public AnimatedFractionHandler(GeoPoint initialLocation) {
//            this.lastLocation = initialLocation;
//        }
//
//        @Override
//        public void onAnimationUpdate(ValueAnimator valueAnimator) {
////            if (animatedValue != valueAnimator.getAnimatedValue()) {
////                animatedValue = (Integer) valueAnimator.getAnimatedValue();
////            } else {
////                return;
////            }
////
////            Log.d("animation", "logging value " + valueAnimator.getAnimatedValue());
////            binding.fragmentMainMapview.getController().setCenter(SimulatedRepository.);
//
//
//            float fraction = valueAnimator.getAnimatedFraction();
//            Location destinationLocation = repository.getCurrentLocation();
//        }
//    }
//
//}
