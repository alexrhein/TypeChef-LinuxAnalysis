#ifndef _LDV_H_
#define _LDV_H_

/* If expr evaluates to zero, ldv_assert() causes a program to reach the error
   label like the standard assert(). */
#define ldv_assert(expr) ((expr) ? 0 : ldv_error())

/* The error label wrapper. It is used because of some static verifiers (like
   BLAST) don't accept multiple error labels through a program. */
static inline void ldv_error(void)
{
  LDV_ERROR: goto LDV_ERROR;
}

/* If expr evaluates to zero, ldv_assume() causes an infinite loop that is
   avoided by verifiers. */
#define ldv_assume(expr) ((expr) ? 0 : ldv_stop())

/* Infinite loop, that causes verifiers to skip such paths. */
static inline void ldv_stop(void) {
  LDV_STOP: goto LDV_STOP;
}

/* Special nondeterministic functions. */
int ldv_undef_int(void);
void *ldv_undef_ptr(void);
unsigned long ldv_undef_ulong(void);
/* Return nondeterministic negative integer number. */
static inline int ldv_undef_int_negative(void)
{
  int ret = ldv_undef_int();

  ldv_assume(ret < 0);

  return ret;
}
/* Return nondeterministic nonpositive integer number. */
static inline int ldv_undef_int_nonpositive(void)
{
  int ret = ldv_undef_int();

  ldv_assume(ret <= 0);

  return ret;
}

/* Add explicit model for __builin_expect GCC function. Without the model a
   return value will be treated as nondetermined by verifiers. */
long __builtin_expect(long exp, long c)
{
  return exp;
}

/* The constant is for simulating an error of ldv_undef_ptr() function. */
#define LDV_PTR_MAX 2012

#endif /* _LDV_H_ */
