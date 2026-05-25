/* Weak fallback for __libc_single_threaded (added in glibc 2.32).
   GCC 11+ generates a reference to this symbol in shared_ptr's _M_release()
   as a single-thread optimisation. The Kotlin/Native sysroot targets glibc 2.19
   which predates this symbol, so the KN linker cannot find it.
   A weak definition here resolves the link; at runtime on glibc 2.32+ the
   dynamic linker overrides it with the real symbol from libc.so.6.
   On older glibc the fallback value 0 (multi-threaded) is used, which is
   always safe — shared_ptr simply skips the single-thread fast path. */
__attribute__((weak)) char __libc_single_threaded = 0;
