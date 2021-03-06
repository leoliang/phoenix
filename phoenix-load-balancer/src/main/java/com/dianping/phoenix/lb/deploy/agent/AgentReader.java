package com.dianping.phoenix.lb.deploy.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.dianping.phoenix.lb.deploy.model.DeployAgentStatus;

public class AgentReader {
    private static final String SEPARATOR  = "--9ed2b78c112fbd17a8511812c554da62941629a8--";

    private static final String TERMINATOR = "--255220d51dc7fb4aacddadedfe252a346da267d4--";

    private Reader              m_reader;

    private boolean             m_last;

    private StringBuilder       m_sb       = new StringBuilder(4096);

    public AgentReader(Reader reader) {
        m_reader = reader;
    }

    public boolean hasNext() {
        return !m_last;
    }

    public List<String> next(AgentClientResult result) throws IOException {
        char[] data = new char[2048];
        String segment = null;

        while (true) {
            int len = 0;
            len = m_reader.read(data);

            if (len > 0) {
                m_sb.append(data, 0, len);
            }

            int pos = m_sb.indexOf(SEPARATOR);
            int sl = SEPARATOR.length();

            if (pos > 0 && pos + sl < m_sb.length() && m_sb.charAt(pos + sl) == '\r') {
                sl++;
            }

            if (pos > 0 && pos + sl < m_sb.length() && m_sb.charAt(pos + sl) == '\n') {
                sl++;
            }

            if (pos >= 0) {
                segment = m_sb.substring(0, pos);

                m_sb.delete(0, pos + sl);
                break;
            } else {
                pos = m_sb.indexOf(TERMINATOR);

                if (pos >= 0) {
                    m_last = true;
                    segment = m_sb.substring(0, pos);
                    break;
                }
            }
        }

        if (segment != null) {
            if (segment.length() == 0) {
                return new ArrayList<String>();
            }

            BufferedReader reader = new BufferedReader(new StringReader(segment), segment.length());
            List<String> lines = new ArrayList<String>();
            boolean header = true;

            while (true) {
                String line = reader.readLine();

                if (line == null) {
                    break;
                } else {
                    if (header) {
                        if (line.length() == 0) { // first blank line
                            header = false;
                        } else if (line.startsWith("Progress:")) {
                            int pos1 = "Progress:".length();
                            int pos2 = line.indexOf('/', pos1);

                            int current = Integer.parseInt(line.substring(pos1, pos2).trim());
                            int total = Integer.parseInt(line.substring(pos2 + 1).trim());
                            result.setProcessPct(current * 100 / total);
                        } else if (line.startsWith("Status:")) {
                            int pos = "Status:".length();

                            String status = line.substring(pos).trim();

                            if ("failed".equals(status)) {
                                result.setStatus(DeployAgentStatus.FAILED);
                                result.setProcessPct(100);
                            } else if ("successful".equals(status)) {
                                result.setStatus(DeployAgentStatus.SUCCESS);
                                result.setProcessPct(100);
                            }
                        } else if (line.startsWith("Step:")) {
                            int pos = "Step:".length();

                            result.setCurrentStep(line.substring(pos).trim());
                        }
                    } else {
                        lines.add(line);
                    }
                }
            }

            return lines;
        }

        throw new IOException(String.format("Invalid protocol! segment: %s\r\n", segment));
    }
}