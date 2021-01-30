package pbouda.jfr.linux;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LineUtilsTest {

    @Test
    public void parseLine() {
        long result = LineUtils.longNumber("voluntary_ctxt_switches:    12345");
        assertEquals(12345, result);
    }

    @Test
    public void containsLine() {
        String content = """
                Name:	java
                Umask:	0002
                State:	S (sleeping)
                Tgid:	210486
                Ngid:	0
                Pid:	210486
                PPid:	210443
                TracerPid:	0
                Uid:	1000	1000	1000	1000
                Gid:	1000	1000	1000	1000
                FDSize:	2048
                Groups:	4 24 27 30 46 112 128 130 131 1000\s
                NStgid:	210486
                NSpid:	210486
                NSpgid:	1261
                NSsid:	1261
                VmPeak:	11319248 kB
                VmSize:	11290568 kB
                VmLck:	       0 kB
                VmPin:	       0 kB
                VmHWM:	 1849232 kB
                VmRSS:	 1849228 kB
                RssAnon:	 1724372 kB
                RssFile:	  124708 kB
                RssShmem:	     148 kB
                VmData:	 2120816 kB
                VmStk:	     132 kB
                VmExe:	       4 kB
                VmLib:	  160628 kB
                VmPTE:	    4640 kB
                VmSwap:	       0 kB
                HugetlbPages:	       0 kB
                CoreDumping:	0
                THP_enabled:	1
                Threads:	94
                SigQ:	10/78754
                SigPnd:	0000000000000000
                ShdPnd:	0000000000000000
                SigBlk:	0000000000000000
                SigIgn:	0000000000001000
                SigCgt:	2000000181004ccf
                CapInh:	0000000000000000
                CapPrm:	0000000000000000
                CapEff:	0000000000000000
                CapBnd:	000000ffffffffff
                CapAmb:	0000000000000000
                NoNewPrivs:	0
                Seccomp:	0
                Speculation_Store_Bypass:	thread vulnerable
                Cpus_allowed:	ff
                Cpus_allowed_list:	0-7
                Mems_allowed:	00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000001
                Mems_allowed_list:	0
                voluntary_ctxt_switches:	1
                nonvoluntary_ctxt_switches:	0
                """;

        List<String> lines = content.lines()
                .collect(Collectors.toUnmodifiableList());

        assertTrue(LineUtils.containsLine(lines, "voluntary_ctxt_switches"));
        assertTrue(LineUtils.containsLine(lines, "nonvoluntary_ctxt_switches"));
        assertFalse(LineUtils.containsLine(List.of("whaat"), "voluntary_ctxt_switches"));
    }

}