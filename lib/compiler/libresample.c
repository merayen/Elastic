/**********************************************************************

  resample.h

  Real-time library interface by Dominic Mazzoni

  Based on resample-1.7:
    http://www-ccrma.stanford.edu/~jos/resample/

  Dual-licensed as LGPL and BSD; see README.md and LICENSE* files.

**********************************************************************/

#ifndef LIBRESAMPLE_INCLUDED
#define LIBRESAMPLE_INCLUDED

#ifdef __cplusplus
extern "C" {
#endif	/* __cplusplus */

void *resample_open(int      highQuality,
                    double   minFactor,
                    double   maxFactor);

void *resample_dup(const void *handle);

int resample_get_filter_width(const void *handle);

int resample_process(void   *handle,
                     double  factor,
                     float  *inBuffer,
                     int     inBufferLen,
                     int     lastFlag,
                     int    *inBufferUsed,
                     float  *outBuffer,
                     int     outBufferLen);

void resample_close(void *handle);

#ifdef __cplusplus
}		/* extern "C" */
#endif	/* __cplusplus */

#endif /* LIBRESAMPLE_INCLUDED */
/**********************************************************************

  resample_defs.h

  Real-time library interface by Dominic Mazzoni

  Based on resample-1.7:
    http://www-ccrma.stanford.edu/~jos/resample/

  Dual-licensed as LGPL and BSD; see README.md and LICENSE* files.

**********************************************************************/

#ifndef __RESAMPLE_DEFS__
#define __RESAMPLE_DEFS__

#if !defined(WIN32) && !defined(__CYGWIN__)

#endif

#ifndef TRUE
#define TRUE  1
#endif

#ifndef FALSE
#define FALSE 0
#endif

#ifndef PI
#define PI (3.14159265358979232846)
#endif

#ifndef PI2
#define PI2 (6.28318530717958465692)
#endif

#define D2R (0.01745329348)          /* (2*pi)/360 */
#define R2D (57.29577951)            /* 360/(2*pi) */

#ifndef MAX
#define MAX(x,y) ((x)>(y) ?(x):(y))
#endif
#ifndef MIN
#define MIN(x,y) ((x)<(y) ?(x):(y))
#endif

#ifndef ABS
#define ABS(x)   ((x)<0   ?(-(x)):(x))
#endif

#ifndef SGN
#define SGN(x)   ((x)<0   ?(-1):((x)==0?(0):(1)))
#endif

#if HAVE_INTTYPES_H
  #include <inttypes.h>
  typedef char           BOOL;
  typedef int32_t        WORD;
  typedef uint32_t       UWORD;
#else
  typedef char           BOOL;
  typedef int            WORD;
  typedef unsigned int   UWORD;
#endif

#ifdef DEBUG
#define INLINE
#else
#define INLINE inline
#endif

/* Accuracy */

#define Npc 4096

/* Function prototypes */

int lrsSrcUp(float X[], float Y[], double factor, double *Time,
             UWORD Nx, UWORD Nwing, float LpScl,
             float Imp[], float ImpD[], BOOL Interp);

int lrsSrcUD(float X[], float Y[], double factor, double *Time,
             UWORD Nx, UWORD Nwing, float LpScl,
             float Imp[], float ImpD[], BOOL Interp);

#endif
/**********************************************************************

  resamplesubs.c

  Real-time library interface by Dominic Mazzoni

  Based on resample-1.7:
    http://www-ccrma.stanford.edu/~jos/resample/

  Dual-licensed as LGPL and BSD; see README.md and LICENSE* files.

**********************************************************************/

/* Definitions */


/*
 * FilterUp() - Applies a filter to a given sample when up-converting.
 * FilterUD() - Applies a filter to a given sample when up- or down-
 */

float lrsFilterUp(float Imp[], float ImpD[], UWORD Nwing, BOOL Interp,
                  float *Xp, double Ph, int Inc);

float lrsFilterUD(float Imp[], float ImpD[], UWORD Nwing, BOOL Interp,
                  float *Xp, double Ph, int Inc, double dhb);

void lrsLpFilter(double c[], int N, double frq, double Beta, int Num);
/**********************************************************************

  resamplesubs.c

  Real-time library interface by Dominic Mazzoni

  Based on resample-1.7:
    http://www-ccrma.stanford.edu/~jos/resample/

  Dual-licensed as LGPL and BSD; see README.md and LICENSE* files.

  This file provides Kaiser-windowed low-pass filter support,
  including a function to create the filter coefficients, and
  two functions to apply the filter at a particular point.

**********************************************************************/

/* Definitions */




#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <math.h>

/* LpFilter()
 *
 * reference: "Digital Filters, 2nd edition"
 *            R.W. Hamming, pp. 178-179
 *
 * Izero() computes the 0th order modified bessel function of the first kind.
 *    (Needed to compute Kaiser window).
 *
 * LpFilter() computes the coeffs of a Kaiser-windowed low pass filter with
 *    the following characteristics:
 *
 *       c[]  = array in which to store computed coeffs
 *       frq  = roll-off frequency of filter
 *       N    = Half the window length in number of coeffs
 *       Beta = parameter of Kaiser window
 *       Num  = number of coeffs before 1/frq
 *
 * Beta trades the rejection of the lowpass filter against the transition
 *    width from passband to stopband.  Larger Beta means a slower
 *    transition and greater stopband rejection.  See Rabiner and Gold
 *    (Theory and Application of DSP) under Kaiser windows for more about
 *    Beta.  The following table from Rabiner and Gold gives some feel
 *    for the effect of Beta:
 *
 * All ripples in dB, width of transition band = D*N where N = window length
 *
 *               BETA    D       PB RIP   SB RIP
 *               2.120   1.50  +-0.27      -30
 *               3.384   2.23    0.0864    -40
 *               4.538   2.93    0.0274    -50
 *               5.658   3.62    0.00868   -60
 *               6.764   4.32    0.00275   -70
 *               7.865   5.0     0.000868  -80
 *               8.960   5.7     0.000275  -90
 *               10.056  6.4     0.000087  -100
 */

#define IzeroEPSILON 1E-21               /* Max error acceptable in Izero */

static double Izero(double x)
{
   double sum, u, halfx, temp;
   int n;

   sum = u = n = 1;
   halfx = x/2.0;
   do {
      temp = halfx/(double)n;
      n += 1;
      temp *= temp;
      u *= temp;
      sum += u;
   } while (u >= IzeroEPSILON*sum);
   return(sum);
}

void lrsLpFilter(double c[], int N, double frq, double Beta, int Num)
{
   double IBeta, temp, temp1, inm1;
   int i;

   /* Calculate ideal lowpass filter impulse response coefficients: */
   c[0] = 2.0*frq;
   for (i=1; i<N; i++) {
      temp = PI*(double)i/(double)Num;
      c[i] = sin(2.0*temp*frq)/temp; /* Analog sinc function, cutoff = frq */
   }

   /* 
    * Calculate and Apply Kaiser window to ideal lowpass filter.
    * Note: last window value is IBeta which is NOT zero.
    * You're supposed to really truncate the window here, not ramp
    * it to zero. This helps reduce the first sidelobe. 
    */
   IBeta = 1.0/Izero(Beta);
   inm1 = 1.0/((double)(N-1));
   for (i=1; i<N; i++) {
      temp = (double)i * inm1;
      temp1 = 1.0 - temp*temp;
      temp1 = (temp1<0? 0: temp1); /* make sure it's not negative since
                                      we're taking the square root - this
                                      happens on Pentium 4's due to tiny
                                      roundoff errors */
      c[i] *= Izero(Beta*sqrt(temp1)) * IBeta;
   }
}

float lrsFilterUp(float Imp[],  /* impulse response */
                  float ImpD[], /* impulse response deltas */
                  UWORD Nwing,  /* len of one wing of filter */
                  BOOL Interp,  /* Interpolate coefs using deltas? */
                  float *Xp,    /* Current sample */
                  double Ph,    /* Phase */
                  int Inc)    /* increment (1 for right wing or -1 for left) */
{
   float *Hp, *Hdp = NULL, *End;
   double a = 0;
   float v, t;

   Ph *= Npc; /* Npc is number of values per 1/delta in impulse response */
   
   v = 0.0; /* The output value */
   Hp = &Imp[(int)Ph];
   End = &Imp[Nwing];
   if (Interp) {
      Hdp = &ImpD[(int)Ph];
      a = Ph - floor(Ph); /* fractional part of Phase */
   }

   if (Inc == 1)		/* If doing right wing...              */
   {				      /* ...drop extra coeff, so when Ph is  */
      End--;			/*    0.5, we don't do too many mult's */
      if (Ph == 0)		/* If the phase is zero...           */
      {			         /* ...then we've already skipped the */
         Hp += Npc;		/*    first sample, so we must also  */
         Hdp += Npc;		/*    skip ahead in Imp[] and ImpD[] */
      }
   }

   if (Interp)
      while (Hp < End) {
         t = *Hp;		/* Get filter coeff */
         t += (*Hdp)*a; /* t is now interp'd filter coeff */
         Hdp += Npc;		/* Filter coeff differences step */
         t *= *Xp;		/* Mult coeff by input sample */
         v += t;			/* The filter output */
         Hp += Npc;		/* Filter coeff step */
         Xp += Inc;		/* Input signal step. NO CHECK ON BOUNDS */
      } 
   else 
      while (Hp < End) {
         t = *Hp;		/* Get filter coeff */
         t *= *Xp;		/* Mult coeff by input sample */
         v += t;			/* The filter output */
         Hp += Npc;		/* Filter coeff step */
         Xp += Inc;		/* Input signal step. NO CHECK ON BOUNDS */
      }
   
   return v;
}

float lrsFilterUD(float Imp[],  /* impulse response */
                  float ImpD[], /* impulse response deltas */
                  UWORD Nwing,  /* len of one wing of filter */
                  BOOL Interp,  /* Interpolate coefs using deltas? */
                  float *Xp,    /* Current sample */
                  double Ph,    /* Phase */
                  int Inc,    /* increment (1 for right wing or -1 for left) */
                  double dhb) /* filter sampling period */
{
   float a;
   float *Hp, *Hdp, *End;
   float v, t;
   double Ho;
    
   v = 0.0; /* The output value */
   Ho = Ph*dhb;
   End = &Imp[Nwing];
   if (Inc == 1)		/* If doing right wing...              */
   {				      /* ...drop extra coeff, so when Ph is  */
      End--;			/*    0.5, we don't do too many mult's */
      if (Ph == 0)		/* If the phase is zero...           */
         Ho += dhb;		/* ...then we've already skipped the */
   }				         /*    first sample, so we must also  */
                        /*    skip ahead in Imp[] and ImpD[] */

   if (Interp)
      while ((Hp = &Imp[(int)Ho]) < End) {
         t = *Hp;		/* Get IR sample */
         Hdp = &ImpD[(int)Ho];  /* get interp bits from diff table*/
         a = Ho - floor(Ho);	  /* a is logically between 0 and 1 */
         t += (*Hdp)*a; /* t is now interp'd filter coeff */
         t *= *Xp;		/* Mult coeff by input sample */
         v += t;			/* The filter output */
         Ho += dhb;		/* IR step */
         Xp += Inc;		/* Input signal step. NO CHECK ON BOUNDS */
      }
   else 
      while ((Hp = &Imp[(int)Ho]) < End) {
         t = *Hp;		/* Get IR sample */
         t *= *Xp;		/* Mult coeff by input sample */
         v += t;			/* The filter output */
         Ho += dhb;		/* IR step */
         Xp += Inc;		/* Input signal step. NO CHECK ON BOUNDS */
      }

   return v;
}
/**********************************************************************

  resamplesubs.c

  Real-time library interface by Dominic Mazzoni

  Based on resample-1.7:
    http://www-ccrma.stanford.edu/~jos/resample/

  Dual-licensed as LGPL and BSD; see README.md and LICENSE* files.

**********************************************************************/

/* Definitions */


/*
 * FilterUp() - Applies a filter to a given sample when up-converting.
 * FilterUD() - Applies a filter to a given sample when up- or down-
 */

float lrsFilterUp(float Imp[], float ImpD[], UWORD Nwing, BOOL Interp,
                  float *Xp, double Ph, int Inc);

float lrsFilterUD(float Imp[], float ImpD[], UWORD Nwing, BOOL Interp,
                  float *Xp, double Ph, int Inc, double dhb);

void lrsLpFilter(double c[], int N, double frq, double Beta, int Num);
/**********************************************************************

  resample.c

  Real-time library interface by Dominic Mazzoni

  Based on resample-1.7:
    http://www-ccrma.stanford.edu/~jos/resample/

  Dual-licensed as LGPL and BSD; see README.md and LICENSE* files.

  This is the main source file, implementing all of the API
  functions and handling all of the buffering logic.

**********************************************************************/

/* External interface */


/* Definitions */




#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <string.h>

typedef struct {
   float  *Imp;
   float  *ImpD;
   float   LpScl;
   UWORD   Nmult;
   UWORD   Nwing;
   double  minFactor;
   double  maxFactor;
   UWORD   XSize;
   float  *X;
   UWORD   Xp; /* Current "now"-sample pointer for input */
   UWORD   Xread; /* Position to put new samples */
   UWORD   Xoff;
   UWORD   YSize;
   float  *Y;
   UWORD   Yp;
   double  Time;
} rsdata;

void *resample_dup(const void *	handle)
{
   const rsdata *cpy = (const rsdata *)handle;
   rsdata *hp = (rsdata *)malloc(sizeof(rsdata));

   hp->minFactor = cpy->minFactor;
   hp->maxFactor = cpy->maxFactor;
   hp->Nmult = cpy->Nmult;
   hp->LpScl = cpy->LpScl;
   hp->Nwing = cpy->Nwing;

   hp->Imp = (float *)malloc(hp->Nwing * sizeof(float));
   memcpy(hp->Imp, cpy->Imp, hp->Nwing * sizeof(float));
   hp->ImpD = (float *)malloc(hp->Nwing * sizeof(float));
   memcpy(hp->ImpD, cpy->ImpD, hp->Nwing * sizeof(float));

   hp->Xoff = cpy->Xoff;
   hp->XSize = cpy->XSize;
   hp->X = (float *)malloc((hp->XSize + hp->Xoff) * sizeof(float));
   memcpy(hp->X, cpy->X, (hp->XSize + hp->Xoff) * sizeof(float));
   hp->Xp = cpy->Xp;
   hp->Xread = cpy->Xread;
   hp->YSize = cpy->YSize;
   hp->Y = (float *)malloc(hp->YSize * sizeof(float));
   memcpy(hp->Y, cpy->Y, hp->YSize * sizeof(float));
   hp->Yp = cpy->Yp;
   hp->Time = cpy->Time;
   
   return (void *)hp;
}

void *resample_open(int highQuality, double minFactor, double maxFactor)
{
   double *Imp64;
   double Rolloff, Beta;
   rsdata *hp;
   UWORD   Xoff_min, Xoff_max;
   int i;

   /* Just exit if we get invalid factors */
   if (minFactor <= 0.0 || maxFactor <= 0.0 || maxFactor < minFactor) {
      #if DEBUG
      fprintf(stderr,
              "libresample: "
              "minFactor and maxFactor must be positive real numbers,\n"
              "and maxFactor should be larger than minFactor.\n");
      #endif
      return 0;
   }

   hp = (rsdata *)malloc(sizeof(rsdata));

   hp->minFactor = minFactor;
   hp->maxFactor = maxFactor;
 
   if (highQuality)
      hp->Nmult = 35;
   else
      hp->Nmult = 11;

   hp->LpScl = 1.0;
   hp->Nwing = Npc*(hp->Nmult-1)/2; /* # of filter coeffs in right wing */

   Rolloff = 0.90;
   Beta = 6;

   Imp64 = (double *)malloc(hp->Nwing * sizeof(double));

   lrsLpFilter(Imp64, hp->Nwing, 0.5*Rolloff, Beta, Npc);

   hp->Imp = (float *)malloc(hp->Nwing * sizeof(float));
   hp->ImpD = (float *)malloc(hp->Nwing * sizeof(float));
   for(i=0; i<hp->Nwing; i++)
      hp->Imp[i] = Imp64[i];

   /* Storing deltas in ImpD makes linear interpolation
      of the filter coefficients faster */
   for (i=0; i<hp->Nwing-1; i++)
      hp->ImpD[i] = hp->Imp[i+1] - hp->Imp[i];

   /* Last coeff. not interpolated */
   hp->ImpD[hp->Nwing-1] = - hp->Imp[hp->Nwing-1];

   free(Imp64);

   /* Calc reach of LP filter wing (plus some creeping room) */
   Xoff_min = ((hp->Nmult+1)/2.0) * MAX(1.0, 1.0/minFactor) + 10;
   Xoff_max = ((hp->Nmult+1)/2.0) * MAX(1.0, 1.0/maxFactor) + 10;
   hp->Xoff = MAX(Xoff_min, Xoff_max);

   /* Make the inBuffer size at least 4096, but larger if necessary
      in order to store the minimum reach of the LP filter and then some.
      Then allocate the buffer an extra Xoff larger so that
      we can zero-pad up to Xoff zeros at the end when we reach the
      end of the input samples. */
   hp->XSize = MAX(2*hp->Xoff+10, 4096);
   hp->X = (float *)malloc((hp->XSize + hp->Xoff) * sizeof(float));
   hp->Xp = hp->Xoff;
   hp->Xread = hp->Xoff;
   
   /* Need Xoff zeros at begining of X buffer */
   for(i=0; i<hp->Xoff; i++)
      hp->X[i]=0;

   /* Make the outBuffer long enough to hold the entire processed
      output of one inBuffer */
   hp->YSize = (int)(((double)hp->XSize)*maxFactor+2.0);
   hp->Y = (float *)malloc(hp->YSize * sizeof(float));
   hp->Yp = 0;

   hp->Time = (double)hp->Xoff; /* Current-time pointer for converter */
   
   return (void *)hp;
}

int resample_get_filter_width(const void   *handle)
{
   const rsdata *hp = (const rsdata *)handle;
   return hp->Xoff;
}

int resample_process(void   *handle,
                     double  factor,
                     float  *inBuffer,
                     int     inBufferLen,
                     int     lastFlag,
                     int    *inBufferUsed, /* output param */
                     float  *outBuffer,
                     int     outBufferLen)
{
   rsdata *hp = (rsdata *)handle;
   float  *Imp = hp->Imp;
   float  *ImpD = hp->ImpD;
   float  LpScl = hp->LpScl;
   UWORD  Nwing = hp->Nwing;
   BOOL interpFilt = FALSE; /* TRUE means interpolate filter coeffs */
   int outSampleCount;
   UWORD Nout, Ncreep, Nreuse;
   int Nx;
   int i, len;

   #if DEBUG
   fprintf(stderr, "resample_process: in=%d, out=%d lastFlag=%d\n",
           inBufferLen, outBufferLen, lastFlag);
   #endif

   /* Initialize inBufferUsed and outSampleCount to 0 */
   *inBufferUsed = 0;
   outSampleCount = 0;

   if (factor < hp->minFactor || factor > hp->maxFactor) {
      #if DEBUG
      fprintf(stderr,
              "libresample: factor %f is not between "
              "minFactor=%f and maxFactor=%f",
              factor, hp->minFactor, hp->maxFactor);
      #endif
      return -1;
   }

   /* Start by copying any samples still in the Y buffer to the output
      buffer */
   if (hp->Yp && (outBufferLen-outSampleCount)>0) {
      len = MIN(outBufferLen-outSampleCount, hp->Yp);
      for(i=0; i<len; i++)
         outBuffer[outSampleCount+i] = hp->Y[i];
      outSampleCount += len;
      for(i=0; i<hp->Yp-len; i++)
         hp->Y[i] = hp->Y[i+len];
      hp->Yp -= len;
   }

   /* If there are still output samples left, return now - we need
      the full output buffer available to us... */
   if (hp->Yp)
      return outSampleCount;

   /* Account for increased filter gain when using factors less than 1 */
   if (factor < 1)
      LpScl = LpScl*factor;

   for(;;) {

      /* This is the maximum number of samples we can process
         per loop iteration */

      #ifdef DEBUG
      printf("XSize: %d Xoff: %d Xread: %d Xp: %d lastFlag: %d\n",
             hp->XSize, hp->Xoff, hp->Xread, hp->Xp, lastFlag);
      #endif

      /* Copy as many samples as we can from the input buffer into X */
      len = hp->XSize - hp->Xread;

      if (len >= (inBufferLen - (*inBufferUsed)))
         len = (inBufferLen - (*inBufferUsed));

      for(i=0; i<len; i++)
         hp->X[hp->Xread + i] = inBuffer[(*inBufferUsed) + i];

      *inBufferUsed += len;
      hp->Xread += len;

      if (lastFlag && (*inBufferUsed == inBufferLen)) {
         /* If these are the last samples, zero-pad the
            end of the input buffer and make sure we process
            all the way to the end */
         Nx = hp->Xread - hp->Xoff;
         for(i=0; i<hp->Xoff; i++)
            hp->X[hp->Xread + i] = 0;
      }
      else
         Nx = hp->Xread - 2 * hp->Xoff;

      #ifdef DEBUG
      fprintf(stderr, "new len=%d Nx=%d\n", len, Nx);
      #endif

      if (Nx <= 0)
         break;

      /* Resample stuff in input buffer */
      if (factor >= 1) {      /* SrcUp() is faster if we can use it */
         Nout = lrsSrcUp(hp->X, hp->Y, factor, &hp->Time, Nx,
                         Nwing, LpScl, Imp, ImpD, interpFilt);
      }
      else {
         Nout = lrsSrcUD(hp->X, hp->Y, factor, &hp->Time, Nx,
                         Nwing, LpScl, Imp, ImpD, interpFilt);
      }

      #ifdef DEBUG
      printf("Nout: %d\n", Nout);
      #endif
      
      hp->Time -= Nx;         /* Move converter Nx samples back in time */
      hp->Xp += Nx;           /* Advance by number of samples processed */

      /* Calc time accumulation in Time */
      Ncreep = (int)(hp->Time) - hp->Xoff;
      if (Ncreep) {
         hp->Time -= Ncreep;  /* Remove time accumulation */
         hp->Xp += Ncreep;    /* and add it to read pointer */
      }

      /* Copy part of input signal that must be re-used */
      Nreuse = hp->Xread - (hp->Xp - hp->Xoff);

      for (i=0; i<Nreuse; i++)
         hp->X[i] = hp->X[i + (hp->Xp - hp->Xoff)];

      #ifdef DEBUG
      printf("New Xread=%d\n", Nreuse);
      #endif

      hp->Xread = Nreuse;  /* Pos in input buff to read new data into */
      hp->Xp = hp->Xoff;
      
      /* Check to see if output buff overflowed (shouldn't happen!) */
      if (Nout > hp->YSize) {
         #ifdef DEBUG
         printf("Nout: %d YSize: %d\n", Nout, hp->YSize);
         #endif
         fprintf(stderr, "libresample: Output array overflow!\n");
         return -1;
      }

      hp->Yp = Nout;

      /* Copy as many samples as possible to the output buffer */
      if (hp->Yp && (outBufferLen-outSampleCount)>0) {
         len = MIN(outBufferLen-outSampleCount, hp->Yp);
         for(i=0; i<len; i++)
            outBuffer[outSampleCount+i] = hp->Y[i];
         outSampleCount += len;
         for(i=0; i<hp->Yp-len; i++)
            hp->Y[i] = hp->Y[i+len];
         hp->Yp -= len;
      }

      /* If there are still output samples left, return now,
         since we need the full output buffer available */
      if (hp->Yp)
         break;
   }

   return outSampleCount;
}

void resample_close(void *handle)
{
   rsdata *hp = (rsdata *)handle;
   free(hp->X);
   free(hp->Y);
   free(hp->Imp);
   free(hp->ImpD);
   free(hp);
}

/**********************************************************************

  resample_defs.h

  Real-time library interface by Dominic Mazzoni

  Based on resample-1.7:
    http://www-ccrma.stanford.edu/~jos/resample/

  Dual-licensed as LGPL and BSD; see README.md and LICENSE* files.

**********************************************************************/

#ifndef __RESAMPLE_DEFS__
#define __RESAMPLE_DEFS__

#if !defined(WIN32) && !defined(__CYGWIN__)

#endif

#ifndef TRUE
#define TRUE  1
#endif

#ifndef FALSE
#define FALSE 0
#endif

#ifndef PI
#define PI (3.14159265358979232846)
#endif

#ifndef PI2
#define PI2 (6.28318530717958465692)
#endif

#define D2R (0.01745329348)          /* (2*pi)/360 */
#define R2D (57.29577951)            /* 360/(2*pi) */

#ifndef MAX
#define MAX(x,y) ((x)>(y) ?(x):(y))
#endif
#ifndef MIN
#define MIN(x,y) ((x)<(y) ?(x):(y))
#endif

#ifndef ABS
#define ABS(x)   ((x)<0   ?(-(x)):(x))
#endif

#ifndef SGN
#define SGN(x)   ((x)<0   ?(-1):((x)==0?(0):(1)))
#endif

#if HAVE_INTTYPES_H
  #include <inttypes.h>
  typedef char           BOOL;
  typedef int32_t        WORD;
  typedef uint32_t       UWORD;
#else
  typedef char           BOOL;
  typedef int            WORD;
  typedef unsigned int   UWORD;
#endif

#ifdef DEBUG
#define INLINE
#else
#define INLINE inline
#endif

/* Accuracy */

#define Npc 4096

/* Function prototypes */

int lrsSrcUp(float X[], float Y[], double factor, double *Time,
             UWORD Nx, UWORD Nwing, float LpScl,
             float Imp[], float ImpD[], BOOL Interp);

int lrsSrcUD(float X[], float Y[], double factor, double *Time,
             UWORD Nx, UWORD Nwing, float LpScl,
             float Imp[], float ImpD[], BOOL Interp);

#endif
/**********************************************************************

  resample_defs.h

  Real-time library interface by Dominic Mazzoni

  Based on resample-1.7:
    http://www-ccrma.stanford.edu/~jos/resample/

  Dual-licensed as LGPL and BSD; see README.md and LICENSE* files.

**********************************************************************/

#ifndef __RESAMPLE_DEFS__
#define __RESAMPLE_DEFS__

#if !defined(WIN32) && !defined(__CYGWIN__)

#endif

#ifndef TRUE
#define TRUE  1
#endif

#ifndef FALSE
#define FALSE 0
#endif

#ifndef PI
#define PI (3.14159265358979232846)
#endif

#ifndef PI2
#define PI2 (6.28318530717958465692)
#endif

#define D2R (0.01745329348)          /* (2*pi)/360 */
#define R2D (57.29577951)            /* 360/(2*pi) */

#ifndef MAX
#define MAX(x,y) ((x)>(y) ?(x):(y))
#endif
#ifndef MIN
#define MIN(x,y) ((x)<(y) ?(x):(y))
#endif

#ifndef ABS
#define ABS(x)   ((x)<0   ?(-(x)):(x))
#endif

#ifndef SGN
#define SGN(x)   ((x)<0   ?(-1):((x)==0?(0):(1)))
#endif

#if HAVE_INTTYPES_H
  #include <inttypes.h>
  typedef char           BOOL;
  typedef int32_t        WORD;
  typedef uint32_t       UWORD;
#else
  typedef char           BOOL;
  typedef int            WORD;
  typedef unsigned int   UWORD;
#endif

#ifdef DEBUG
#define INLINE
#else
#define INLINE inline
#endif

/* Accuracy */

#define Npc 4096

/* Function prototypes */

int lrsSrcUp(float X[], float Y[], double factor, double *Time,
             UWORD Nx, UWORD Nwing, float LpScl,
             float Imp[], float ImpD[], BOOL Interp);

int lrsSrcUD(float X[], float Y[], double factor, double *Time,
             UWORD Nx, UWORD Nwing, float LpScl,
             float Imp[], float ImpD[], BOOL Interp);

#endif
/**********************************************************************

  resamplesubs.c

  Real-time library interface by Dominic Mazzoni

  Based on resample-1.7:
    http://www-ccrma.stanford.edu/~jos/resample/

  Dual-licensed as LGPL and BSD; see README.md and LICENSE* files.

  This file provides the routines that do sample-rate conversion
  on small arrays, calling routines from filterkit.

**********************************************************************/

/* Definitions */




#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <string.h>

/* Sampling rate up-conversion only subroutine;
 * Slightly faster than down-conversion;
 */
int lrsSrcUp(float X[],
             float Y[],
             double factor,
             double *TimePtr,
             UWORD Nx,
             UWORD Nwing,
             float LpScl,
             float Imp[],
             float ImpD[],
             BOOL Interp)
{
    float *Xp, *Ystart;
    float v;
    
    double CurrentTime = *TimePtr;
    double dt;                 /* Step through input signal */ 
    double endTime;            /* When Time reaches EndTime, return to user */
    
    dt = 1.0/factor;           /* Output sampling period */
    
    Ystart = Y;
    endTime = CurrentTime + Nx;
    while (CurrentTime < endTime)
    {
        double LeftPhase = CurrentTime-floor(CurrentTime);
        double RightPhase = 1.0 - LeftPhase;

        Xp = &X[(int)CurrentTime]; /* Ptr to current input sample */
        /* Perform left-wing inner product */
        v = lrsFilterUp(Imp, ImpD, Nwing, Interp, Xp,
                        LeftPhase, -1);
        /* Perform right-wing inner product */
        v += lrsFilterUp(Imp, ImpD, Nwing, Interp, Xp+1, 
                         RightPhase, 1);

        v *= LpScl;   /* Normalize for unity filter gain */

        *Y++ = v;               /* Deposit output */
        CurrentTime += dt;      /* Move to next sample by time increment */
    }

    *TimePtr = CurrentTime;
    return (Y - Ystart);        /* Return the number of output samples */
}

/* Sampling rate conversion subroutine */

int lrsSrcUD(float X[],
             float Y[],
             double factor,
             double *TimePtr,
             UWORD Nx,
             UWORD Nwing,
             float LpScl,
             float Imp[],
             float ImpD[],
             BOOL Interp)
{
    float *Xp, *Ystart;
    float v;

    double CurrentTime = (*TimePtr);
    double dh;                 /* Step through filter impulse response */
    double dt;                 /* Step through input signal */
    double endTime;            /* When Time reaches EndTime, return to user */
    
    dt = 1.0/factor;            /* Output sampling period */
    
    dh = MIN(Npc, factor*Npc);  /* Filter sampling period */
    
    Ystart = Y;
    endTime = CurrentTime + Nx;
    while (CurrentTime < endTime)
    {
        double LeftPhase = CurrentTime-floor(CurrentTime);
        double RightPhase = 1.0 - LeftPhase;

        Xp = &X[(int)CurrentTime];     /* Ptr to current input sample */
        /* Perform left-wing inner product */
        v = lrsFilterUD(Imp, ImpD, Nwing, Interp, Xp,
                        LeftPhase, -1, dh);
        /* Perform right-wing inner product */
        v += lrsFilterUD(Imp, ImpD, Nwing, Interp, Xp+1, 
                         RightPhase, 1, dh);

        v *= LpScl;   /* Normalize for unity filter gain */
        *Y++ = v;               /* Deposit output */
        
        CurrentTime += dt;      /* Move to next sample by time increment */
    }

    *TimePtr = CurrentTime;
    return (Y - Ystart);        /* Return the number of output samples */
}
